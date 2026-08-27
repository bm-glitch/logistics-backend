    // ------------------------------------------------------------------
    // [일괄] 여러 요청건을 한 번에 알리기 (+ 송장번호가 기입된 엑셀 첨부)
    //  - 요청자가 엑셀 한 장으로 여러 주문건을 보냈을 때, 돌아가는 알림도 한 번으로 합칩니다.
    //  - 파일 첨부는 봇 권한(files:write)이 있어야 됩니다. 없으면 텍스트 알림만 보내고
    //    첨부 실패를 호출한 쪽에 알려줘서, 물류팀이 파일만 따로 보낼 수 있게 합니다.
    // ------------------------------------------------------------------

    /**
     * @param lines 요청건별 한 줄 요약 (예: "SR-0168 · CJ대한통운 522559491826")
     * @param fileName 첨부할 엑셀 파일명 (null이면 텍스트만)
     * @param file     첨부할 엑셀 바이트 (null이면 텍스트만)
     * @return 파일까지 첨부해서 보냈으면 true, 텍스트만 보냈으면 false
     */
    public boolean notifyBatchShipment(String channelId, String userId, String requester,
                                       List<String> lines, String fileName, byte[] file) {
        String rawTarget = (userId != null && !userId.isBlank()) ? userId : channelId;
        if (rawTarget == null || rawTarget.isBlank()) {
            log.warn("[Slack] 일괄 알림 대상 없음 — requester={}", requester);
            return false;
        }
        String target = resolveDmChannel(rawTarget);
        if (target == null || target.isBlank()) return false;

        String who = (requester == null || requester.isBlank()) ? "요청" : requester + "님";
        StringBuilder sb = new StringBuilder();
        sb.append("*").append(who).append(" 요청 ").append(lines.size()).append("건 출고가 완료되었습니다.* :package:\n");
        for (String l : lines) sb.append("• ").append(l).append("\n");
        boolean withFile = (file != null && file.length > 0 && fileName != null && !fileName.isBlank());
        sb.append(withFile
                ? "\n첨부한 엑셀 파일에 주문건별 송장번호가 함께 기입돼 있어요."
                : "\n송장번호는 위 목록을 확인해 주세요.");
        String text = sb.toString();

        if (withFile) {
            if (uploadFileToChannel(target, fileName, file, text)) return true;
            log.warn("[Slack] 파일 첨부 실패 — 텍스트 알림만 보냅니다. (봇에 files:write 권한이 있는지 확인해 주세요)");
        }

        // 파일 없이(또는 첨부 실패 시) 텍스트만 — 줄바꿈을 유지해 JSON으로 안전하게 넣습니다.
        String safe = text.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", " ").replace("\n", "\\n");
        String blocks = "[{\"type\":\"section\",\"text\":{\"type\":\"mrkdwn\",\"text\":\"" + safe + "\"}}]";
        postMessage(target, null, who + " 요청 " + lines.size() + "건 출고가 완료되었습니다.", blocks);
        return false;
    }

    /**
     * Slack 파일 업로드(신규 방식 3단계). 봇 권한 files:write 필요.
     *  1) files.getUploadURLExternal — 업로드 주소 발급
     *  2) 발급받은 주소로 파일 본문 전송
     *  3) files.completeUploadExternal — 채널(또는 DM)에 공유 + 코멘트
     */
    private boolean uploadFileToChannel(String channel, String fileName, byte[] bytes, String comment) {
        if (botToken == null || botToken.isBlank()) {
            log.error("[Slack] BOT_TOKEN 미설정 — 파일 업로드를 건너뜁니다.");
            return false;
        }
        try {
            // 1) 업로드 주소 발급
            String form = "filename=" + java.net.URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    + "&length=" + bytes.length;
            JsonNode r1 = callForm("files.getUploadURLExternal", form);
            if (r1 == null || !r1.path("ok").asBoolean(false)) return false;
            String uploadUrl = r1.path("upload_url").asText("");
            String fileId = r1.path("file_id").asText("");
            if (uploadUrl.isBlank() || fileId.isBlank()) return false;

            // 2) 파일 본문 전송 (multipart/form-data)
            String boundary = "----wislandBoundary" + System.nanoTime();
            byte[] head = ("--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n\r\n").getBytes(StandardCharsets.UTF_8);
            byte[] tail = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);
            byte[] body = new byte[head.length + bytes.length + tail.length];
            System.arraycopy(head, 0, body, 0, head.length);
            System.arraycopy(bytes, 0, body, head.length, bytes.length);
            System.arraycopy(tail, 0, body, head.length + bytes.length, tail.length);

            HttpRequest up = HttpRequest.newBuilder()
                    .uri(URI.create(uploadUrl))
                    .timeout(Duration.ofSeconds(25))
                    // 업로드 주소에는 토큰이 이미 포함돼 있어서 Authorization 헤더를 넣지 않습니다.
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();
            HttpResponse<String> upRes = http.send(up, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (upRes.statusCode() / 100 != 2) {
                log.error("[Slack] 파일 본문 업로드 실패: {} {}", upRes.statusCode(), upRes.body());
                return false;
            }

            // 3) 업로드 완료 처리 + 채널 공유
            var filesArr = om.createArrayNode();
            var f = om.createObjectNode();
            f.put("id", fileId);
            f.put("title", fileName);
            filesArr.add(f);
            var done = om.createObjectNode();
            done.set("files", filesArr);
            done.put("channel_id", channel);
            if (comment != null && !comment.isBlank()) done.put("initial_comment", comment);
            JsonNode r3 = callNode("files.completeUploadExternal", om.writeValueAsString(done));
            return r3 != null && r3.path("ok").asBoolean(false);
        } catch (Exception e) {
            log.error("[Slack] 파일 업로드 오류", e);
            return false;
        }
    }

    /** JSON 본문으로 Slack API 호출 후 응답을 그대로 돌려줍니다. (실패 시 null) */
    private JsonNode callNode(String method, String jsonBody) {
        return sendToSlack(method, jsonBody, "application/json; charset=utf-8");
    }

    /** form 본문으로 Slack API 호출 (files.getUploadURLExternal 등은 form만 받습니다) */
    private JsonNode callForm(String method, String formBody) {
        return sendToSlack(method, formBody, "application/x-www-form-urlencoded; charset=utf-8");
    }

    private JsonNode sendToSlack(String method, String body, String contentType) {
        if (botToken == null || botToken.isBlank()) {
            log.error("[Slack] BOT_TOKEN 미설정 — {} 호출을 건너뜁니다.", method);
            return null;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SLACK_API + method))
                    .timeout(Duration.ofSeconds(15))
                    .header("Content-Type", contentType)
                    .header("Authorization", "Bearer " + botToken)
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonNode node = om.readTree(res.body());
            if (!node.path("ok").asBoolean(false)) {
                log.error("[Slack] {} 실패: {}", method, res.body());
            }
            return node;
        } catch (Exception e) {
            log.error("[Slack] {} 호출 오류", method, e);
            return null;
        }
    }
