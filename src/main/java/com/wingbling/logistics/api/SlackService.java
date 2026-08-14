package com.wingbling.logistics.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Slack 연동 담당.
 *  - 요청이 진짜 Slack에서 온 것인지 서명 검증
 *  - 봇이 태그되면 "요청서 작성" 버튼 답글 보내기
 *  - 버튼 누르면 입력 폼(모달) 띄우기
 *  - 폼 제출되면 우리 DB에 저장
 */
@Service
@RequiredArgsConstructor
public class SlackService {

    private static final Logger log = LoggerFactory.getLogger(SlackService.class);
    private static final String SLACK_API = "https://slack.com/api/";

    private final ShipmentRequestService shipmentRequestService;
    private final EzAdminService ezAdminService;
    private final StaffDirectoryService staffDirectory;   // 직원 Slack 주소록 저장
    private final ObjectMapper om = new ObjectMapper();

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${slack.bot-token:}")
    private String botToken;

    @Value("${slack.signing-secret:}")
    private String signingSecret;

    // ------------------------------------------------------------------
    // 1) 서명 검증 — 이 요청이 정말 Slack에서 온 것인지 확인
    // ------------------------------------------------------------------

    /**
     * Signing Secret이 설정되지 않았으면 검증을 건너뜁니다(초기 연결 테스트용).
     * 운영에서는 반드시 SLACK_SIGNING_SECRET 환경변수를 넣어 주세요.
     */
    public boolean verify(String timestamp, String signature, String rawBody) {
        if (signingSecret == null || signingSecret.isBlank()) {
            log.warn("[Slack] SIGNING_SECRET 미설정 — 서명 검증을 건너뜁니다.");
            return true;
        }
        if (timestamp == null || signature == null) return false;

        // 5분보다 오래된 요청은 거부 (재전송 공격 방지)
        try {
            long age = Math.abs(System.currentTimeMillis() / 1000 - Long.parseLong(timestamp));
            if (age > 300) return false;
        } catch (NumberFormatException e) {
            return false;
        }

        try {
            String base = "v0:" + timestamp + ":" + rawBody;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder("v0=");
            for (byte b : hash) sb.append(String.format("%02x", b));
            return constantTimeEquals(sb.toString(), signature);
        } catch (Exception e) {
            log.error("[Slack] 서명 검증 실패", e);
            return false;
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) diff |= a.charAt(i) ^ b.charAt(i);
        return diff == 0;
    }

    // ------------------------------------------------------------------
    // 2) 봇이 태그되면 버튼 답글 보내기
    // ------------------------------------------------------------------

    // 웹 대시보드 주소. 배포 주소가 바뀌면 이 한 줄만 고치면 됩니다.
    private static final String WEB_DASHBOARD_URL = "https://bm-glitch.github.io/logistics-dashboard/";

    // 요청이 수정·취소되면 DM으로 알림을 받을 물류팀 3명의 Slack 사용자 ID.
    // (사람이 바뀌면 이 ID만 교체하면 됩니다.) 유다영 / 백화성 / 배영환
    private static final String[] LOGISTICS_USER_IDS = {
            "U0B6GNL528P", // 유다영
            "U0BAC1G04TV", // 백화성
            "U017EUNG5B2"  // 배영환
    };

    public void handleAppMention(JsonNode event) {
        String channel = text(event, "channel");
        String threadTs = event.hasNonNull("thread_ts")
                ? event.get("thread_ts").asText()
                : text(event, "ts");
        String user = text(event, "user");

        // 나중에 송장번호 알림을 이 사람/채널로 보낼 수 있도록, 링크에 실어서 웹 화면까지 전달합니다.
        String link = WEB_DASHBOARD_URL
                + "?slack_channel=" + urlEnc(channel)
                + "&slack_user=" + urlEnc(user)
                + "#form";

        String blocks = ("""
                [
                  {"type":"section","text":{"type":"mrkdwn",
                    "text":"<@USER_ID> 님, 출고 요청서를 작성해 주세요."}},
                  {"type":"actions","elements":[
                    {"type":"button","style":"primary",
                     "text":{"type":"plain_text","text":"🌐 웹에서 요청서 작성하기"},
                     "url":"LINK_URL"}
                  ]}
                ]
                """)
                .replace("USER_ID", user == null ? "" : user)
                .replace("LINK_URL", link);

        postMessage(channel, threadTs, "출고 요청서를 작성해 주세요: " + link, blocks);
    }

    private String urlEnc(String s) {
        if (s == null) return "";
        return java.net.URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /**
     * 요청자가 봇에게 '개인 DM'을 보냈을 때 — 채널에서 태그했을 때와 똑같이
     * "웹에서 요청서 작성하기" 버튼을 답장합니다. (개인 메시지에서 바로 접수 흐름으로 연결)
     * 여기서 채널은 봇과의 DM 채널(D...)이고, 링크에 요청자 정보를 실어
     * 나중에 완료·송장 알림이 이 사람에게 되돌아갈 수 있게 합니다.
     */
    public void handleDirectMessage(JsonNode event) {
        String channel = text(event, "channel");   // 봇과의 DM 채널 (D...)
        String user = text(event, "user");
        String link = WEB_DASHBOARD_URL
                + "?slack_channel=" + urlEnc(channel)
                + "&slack_user=" + urlEnc(user)
                + "#form";
        String blocks = ("""
                [
                  {"type":"section","text":{"type":"mrkdwn",
                    "text":"<@USER_ID> 님, 출고 요청서를 작성해 주세요."}},
                  {"type":"actions","elements":[
                    {"type":"button","style":"primary",
                     "text":{"type":"plain_text","text":"🌐 웹에서 요청서 작성하기"},
                     "url":"LINK_URL"}
                  ]}
                ]
                """)
                .replace("USER_ID", user == null ? "" : user)
                .replace("LINK_URL", link);
        postMessage(channel, null, "출고 요청서를 작성해 주세요: " + link, blocks);
    }

    /**
     * 요청자가 알아보기 쉬운 이름표를 만듭니다. 예: 2026년08월10일_신혜인_김다래
     * (요청일 = 접수한 날 기준. 요청자·수령인 이름을 뒤에 붙이고, 없으면 자동으로 생략합니다.)
     */
    public String friendlyLabel(java.time.LocalDate date, String requester, String receiver) {
        StringBuilder sb = new StringBuilder();
        if (date != null) sb.append(date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy년MM월dd일")));
        if (requester != null && !requester.isBlank()) { if (sb.length() > 0) sb.append("_"); sb.append(requester.trim()); }
        if (receiver != null && !receiver.isBlank()) { if (sb.length() > 0) sb.append("_"); sb.append(receiver.trim()); }
        return sb.toString();
    }

    /** 물류팀이 '완료 전송'을 눌렀을 때, Slack으로 접수됐던 요청이면 요청자에게 완료를 알려줍니다. */
    public void notifyCompletion(String channelId, String userId, String srNo, String label) {
        String title = (label == null || label.isBlank()) ? srNo : label;
        String blocks = ("""
                [
                  {"type":"section","text":{"type":"mrkdwn","text":
                    "*LABEL* 출고가 완료되었습니다. :white_check_mark:\\n_접수번호 SR_NO_"}}
                ]
                """).replace("LABEL", esc(title)).replace("SR_NO", esc(srNo));

        String target = (userId != null && !userId.isBlank()) ? userId : channelId;
        if (target == null || target.isBlank()) return;
        postMessage(target, null, title + " 출고가 완료되었습니다.", blocks);
    }

    /**
     * 물류팀이 송장번호를 등록했을 때, Slack으로 접수됐던 요청이면 요청자에게 바로 알려줍니다.
     */
    public void notifyTracking(String channelId, String userId, String srNo, String trackingsText, String label) {
        String title = (label == null || label.isBlank()) ? srNo : label;
        // 여러 송장을 줄바꿈(\n)으로 이어붙인 문자열을 그대로 보여줍니다. JSON 안전하게 이스케이프하되 줄바꿈은 유지.
        String tt = (trackingsText == null || trackingsText.isBlank()) ? "-" : trackingsText;
        String ttSafe = tt.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", " ").replace("\n", "\\n");
        String blocks = ("""
                [
                  {"type":"section","text":{"type":"mrkdwn","text":
                    "*LABEL* 송장이 등록되었습니다. :package:\\nTRACKINGS\\n_접수번호 SR_NO_"}}
                ]
                """)
                .replace("LABEL", esc(title))
                .replace("SR_NO", esc(srNo))
                .replace("TRACKINGS", ttSafe);

        String target = (userId != null && !userId.isBlank()) ? userId : channelId;
        if (target == null || target.isBlank()) return;
        postMessage(target, null, title + " 송장이 등록되었습니다", blocks);
    }

    private void postMessage(String channel, String threadTs, String fallback, String blocksJson) {
        try {
            channel = resolveDmChannel(channel);
            var body = om.createObjectNode();
            body.put("channel", channel);
            if (threadTs != null && !threadTs.isBlank()) body.put("thread_ts", threadTs);
            body.put("text", fallback);
            if (blocksJson != null) body.set("blocks", om.readTree(blocksJson));
            call("chat.postMessage", om.writeValueAsString(body));
        } catch (Exception e) {
            log.error("[Slack] 메시지 전송 실패", e);
        }
    }

    // ------------------------------------------------------------------
    // 3) 버튼 클릭 → 입력 폼(모달) 열기
    // ------------------------------------------------------------------

    /** trigger_id는 3초 안에 써야 하므로 즉시 호출합니다. */
    public void openRequestModal(String triggerId, String channelId) {
        String view = MODAL_VIEW.replace("__CHANNEL__", channelId == null ? "" : channelId);
        try {
            var body = om.createObjectNode();
            body.put("trigger_id", triggerId);
            body.set("view", om.readTree(view));
            call("views.open", om.writeValueAsString(body));
        } catch (Exception e) {
            log.error("[Slack] 모달 열기 실패", e);
        }
    }

    private static final String MODAL_VIEW = """
            {
              "type": "modal",
              "callback_id": "shipment_request_form",
              "private_metadata": "__CHANNEL__",
              "title":  {"type":"plain_text","text":"출고 요청서"},
              "submit": {"type":"plain_text","text":"제출"},
              "close":  {"type":"plain_text","text":"취소"},
              "blocks": [
                {"type":"input","block_id":"b_billing",
                 "label":{"type":"plain_text","text":"출고 유형"},
                 "element":{"type":"static_select","action_id":"a_billing",
                   "placeholder":{"type":"plain_text","text":"선택하세요"},
                   "options":[
                     {"text":{"type":"plain_text","text":"무상"},"value":"free"},
                     {"text":{"type":"plain_text","text":"유상"},"value":"paid"}
                   ]}},

                {"type":"input","block_id":"b_team",
                 "label":{"type":"plain_text","text":"요청팀"},
                 "element":{"type":"plain_text_input","action_id":"a_team",
                   "placeholder":{"type":"plain_text","text":"예: 마케팅팀"}}},

                {"type":"input","block_id":"b_requester",
                 "label":{"type":"plain_text","text":"요청자"},
                 "element":{"type":"plain_text_input","action_id":"a_requester",
                   "placeholder":{"type":"plain_text","text":"예: 김민지"}}},

                {"type":"input","block_id":"b_date",
                 "label":{"type":"plain_text","text":"출고 희망일"},
                 "element":{"type":"datepicker","action_id":"a_date",
                   "placeholder":{"type":"plain_text","text":"날짜 선택"}}},

                {"type":"divider"},

                {"type":"section","text":{"type":"mrkdwn","text":
                  "*발주서를 올리면 아래 품목·수령정보가 자동으로 채워집니다.*\n올리지 않으면 직접 입력해 주세요."}},

                {"type":"input","block_id":"b_order_file","optional":true,
                 "label":{"type":"plain_text","text":"📎 발주서 업로드 (.xls/.xlsx)"},
                 "element":{"type":"file_input","action_id":"a_order_file",
                   "filetypes":["xls","xlsx"],"max_files":1}},

                {"type":"input","block_id":"b_item","optional":true,
                 "label":{"type":"plain_text","text":"품목 (발주서 없을 때만 입력)"},
                 "element":{"type":"plain_text_input","action_id":"a_item",
                   "placeholder":{"type":"plain_text","text":"예: 판촉물 세트"}}},

                {"type":"input","block_id":"b_qty","optional":true,
                 "label":{"type":"plain_text","text":"수량 (발주서 없을 때만 입력)"},
                 "element":{"type":"number_input","action_id":"a_qty","is_decimal_allowed":false,
                   "min_value":"1"}},

                {"type":"input","block_id":"b_sku","optional":true,
                 "label":{"type":"plain_text","text":"상품코드 (선택 — 입력하면 실시간 가용재고를 알려드려요)"},
                 "element":{"type":"plain_text_input","action_id":"a_sku",
                   "placeholder":{"type":"plain_text","text":"예: S00011"}}},

                {"type":"divider"},

                {"type":"input","block_id":"b_rcv_name","optional":true,
                 "label":{"type":"plain_text","text":"수령자 이름 (발주서 없을 때만 입력)"},
                 "element":{"type":"plain_text_input","action_id":"a_rcv_name",
                   "placeholder":{"type":"plain_text","text":"예: 김솔이"}}},

                {"type":"input","block_id":"b_rcv_phone","optional":true,
                 "label":{"type":"plain_text","text":"수령자 연락처 (발주서 없을 때만 입력)"},
                 "element":{"type":"plain_text_input","action_id":"a_rcv_phone",
                   "placeholder":{"type":"plain_text","text":"예: 010-1234-5678"}}},

                {"type":"input","block_id":"b_rcv_address","optional":true,
                 "label":{"type":"plain_text","text":"수령 주소 (발주서 없을 때만 입력)"},
                 "element":{"type":"plain_text_input","action_id":"a_rcv_address",
                   "placeholder":{"type":"plain_text","text":"예: 서울시 강남구 ..."}}},

                {"type":"input","block_id":"b_rcv_message","optional":true,
                 "label":{"type":"plain_text","text":"배송 메세지"},
                 "element":{"type":"plain_text_input","action_id":"a_rcv_message",
                   "placeholder":{"type":"plain_text","text":"선택 입력"}}},

                {"type":"input","block_id":"b_note","optional":true,
                 "label":{"type":"plain_text","text":"요청 사유 / 비고"},
                 "element":{"type":"plain_text_input","action_id":"a_note","multiline":true,
                   "placeholder":{"type":"plain_text","text":"선택 입력"}}}
              ]
            }
            """;

    // ------------------------------------------------------------------
    // 4) 폼 제출 → 우리 DB에 저장
    // ------------------------------------------------------------------

    /** 발주서에서 읽어낸 상품 한 줄 */
    private record ProductLine(String name, String option, int qty) {}

    /** 발주서에서 읽어낸 전체 내용 */
    private record ParsedOrder(String receiverName, String receiverPhone,
                               String receiverAddress, String receiverMessage,
                               List<ProductLine> products) {}

    // 발주서(웹화면에서 쓰는 것과 같은 표준 양식) 컬럼명. 순서 무관, 이름으로 찾습니다.
    private static final String[] COL_ITEM = {"상품명"};
    private static final String[] COL_OPT  = {"옵션"};
    private static final String[] COL_QTY  = {"수량"};
    private static final String[] COL_RNAME = {"수령자이름", "수령자 이름"};
    private static final String[] COL_RPHONE = {"수령자연락처", "수령자 연락처"};
    private static final String[] COL_RZIP = {"수령자우편번호", "수령자 우편번호"};
    private static final String[] COL_RADDR = {"수령자주소", "수령자 주소"};
    private static final String[] COL_RMSG = {"배송메세지", "배송메시지", "배송 메세지"};

    /**
     * Slack이 보관 중인 발주서 파일을 다운로드해서 읽습니다.
     * url_private은 봇 토큰으로 인증해야만 내려받을 수 있습니다.
     */
    private ParsedOrder downloadAndParseOrderFile(String urlPrivate) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(urlPrivate))
                .timeout(Duration.ofSeconds(8))
                .header("Authorization", "Bearer " + botToken)
                .GET().build();
        HttpResponse<byte[]> res = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
        if (res.statusCode() != 200) {
            throw new IllegalStateException("파일 다운로드 실패 (" + res.statusCode() + ")");
        }

        try (Workbook wb = WorkbookFactory.create(new ByteArrayInputStream(res.body()))) {
            Sheet sheet = wb.getSheetAt(0);
            Row headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) throw new IllegalStateException("빈 파일입니다.");

            int idxItem = findCol(headerRow, COL_ITEM);
            int idxOpt  = findCol(headerRow, COL_OPT);
            int idxQty  = findCol(headerRow, COL_QTY);
            int idxRName = findCol(headerRow, COL_RNAME);
            int idxRPhone = findCol(headerRow, COL_RPHONE);
            int idxRZip = findCol(headerRow, COL_RZIP);
            int idxRAddr = findCol(headerRow, COL_RADDR);
            int idxRMsg = findCol(headerRow, COL_RMSG);

            List<ProductLine> products = new ArrayList<>();
            String rName = "", rPhone = "", rAddr = "", rMsg = "";
            boolean first = true;

            for (int r = sheet.getFirstRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                String name = cellText(row, idxItem);
                if (name.isBlank()) continue; // 발주서 하단의 빈 서식 줄은 건너뜀

                String opt = cellText(row, idxOpt);
                int qty = 1;
                try {
                    String q = cellText(row, idxQty);
                    qty = Math.max(1, (int) Double.parseDouble(q));
                } catch (Exception ignored) { }
                products.add(new ProductLine(name, opt, qty));

                if (first) {
                    rName = cellText(row, idxRName);
                    rPhone = cellText(row, idxRPhone);
                    String zip = cellText(row, idxRZip);
                    String addr = cellText(row, idxRAddr);
                    rAddr = zip.isBlank() ? addr : ("(" + zip + ") " + addr);
                    rMsg = cellText(row, idxRMsg);
                    first = false;
                }
            }
            if (products.isEmpty()) throw new IllegalStateException("상품 데이터를 찾지 못했습니다.");
            return new ParsedOrder(rName, rPhone, rAddr, rMsg, products);
        }
    }

    private int findCol(Row headerRow, String[] names) {
        for (int c = headerRow.getFirstCellNum(); c < headerRow.getLastCellNum(); c++) {
            Cell cell = headerRow.getCell(c);
            if (cell == null) continue;
            String v = cell.getStringCellValue().trim();
            for (String name : names) if (v.equals(name)) return c;
        }
        return -1;
    }

    private String cellText(Row row, int idx) {
        if (idx < 0) return "";
        Cell cell = row.getCell(idx);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                yield (v == Math.floor(v)) ? String.valueOf((long) v) : String.valueOf(v);
            }
            case STRING -> cell.getStringCellValue().trim();
            case FORMULA -> cell.getStringCellValue().trim();
            default -> "";
        };
    }

    /**
     * 모달 제출 처리. Slack은 이 응답을 3초 안에 받아야 하므로 동기로 실행합니다.
     * 반환값이 빈 문자열이면 정상 접수(모달 닫힘), 아니면 Slack이 보여줄 오류 JSON입니다.
     */
    public String handleViewSubmissionSync(JsonNode payload) {
        JsonNode values = payload.path("view").path("state").path("values");
        String channel = payload.path("view").path("private_metadata").asText("");
        String userName = payload.path("user").path("username").asText("");
        String userId = payload.path("user").path("id").asText("");

        String billing   = values.path("b_billing").path("a_billing")
                .path("selected_option").path("value").asText("free");
        String team      = values.path("b_team").path("a_team").path("value").asText("");
        String requester = values.path("b_requester").path("a_requester").path("value").asText("");
        String date      = values.path("b_date").path("a_date").path("selected_date").asText("");
        String note      = values.path("b_note").path("a_note").path("value").asText("");

        String manualItem   = values.path("b_item").path("a_item").path("value").asText("");
        String manualQtyRaw = values.path("b_qty").path("a_qty").path("value").asText("");
        String manualRName  = values.path("b_rcv_name").path("a_rcv_name").path("value").asText("");
        String manualRPhone = values.path("b_rcv_phone").path("a_rcv_phone").path("value").asText("");
        String manualRAddr  = values.path("b_rcv_address").path("a_rcv_address").path("value").asText("");
        String manualRMsg   = values.path("b_rcv_message").path("a_rcv_message").path("value").asText("");
        String sku          = values.path("b_sku").path("a_sku").path("value").asText("");

        JsonNode files = values.path("b_order_file").path("a_order_file").path("files");
        boolean hasFile = files.isArray() && files.size() > 0;

        List<ProductLine> products;
        String rName, rPhone, rAddr, rMsg;

        if (hasFile) {
            String urlPrivate = files.get(0).path("url_private").asText("");
            try {
                ParsedOrder parsed = downloadAndParseOrderFile(urlPrivate);
                products = parsed.products();
                rName = parsed.receiverName();
                rPhone = parsed.receiverPhone();
                rAddr = parsed.receiverAddress();
                rMsg = parsed.receiverMessage();
            } catch (Exception e) {
                log.error("[Slack] 발주서 파싱 실패", e);
                return errorJson("b_order_file",
                        "발주서를 읽지 못했습니다 (" + e.getMessage() + "). 직접 입력해 주세요.");
            }
        } else {
            // 파일이 없으면 직접 입력한 값으로 진행 — 필수 항목 검증
            if (manualItem.isBlank()) return errorJson("b_item", "발주서가 없으면 품목을 입력해 주세요.");
            int qty;
            try { qty = Math.max(1, Integer.parseInt(manualQtyRaw.trim())); }
            catch (Exception e) { return errorJson("b_qty", "발주서가 없으면 수량을 입력해 주세요."); }
            if (manualRName.isBlank()) return errorJson("b_rcv_name", "발주서가 없으면 수령자 이름을 입력해 주세요.");
            if (manualRPhone.isBlank()) return errorJson("b_rcv_phone", "발주서가 없으면 연락처를 입력해 주세요.");
            if (manualRAddr.isBlank()) return errorJson("b_rcv_address", "발주서가 없으면 주소를 입력해 주세요.");

            products = List.of(new ProductLine(manualItem, "", qty));
            rName = manualRName; rPhone = manualRPhone; rAddr = manualRAddr; rMsg = manualRMsg;
        }

        LocalDate wantDate;
        try { wantDate = LocalDate.parse(date); }
        catch (Exception e) { return errorJson("b_date", "출고 희망일을 선택해 주세요."); }

        String itemName = products.get(0).name();
        int totalQty = products.stream().mapToInt(ProductLine::qty).sum();

        String productsJson;
        try {
            var arr = om.createArrayNode();
            for (ProductLine p : products) {
                var row = om.createObjectNode();
                row.put("code", "");
                row.put("name", p.name());
                row.put("option", p.option());
                row.put("qty", p.qty());
                arr.add(row);
            }
            productsJson = om.writeValueAsString(arr);
        } catch (Exception e) {
            productsJson = "[]";
        }

        String srNo;
        String label;
        try {
            var saved = shipmentRequestService.create(new CreateRequestDto(
                    team, requester, itemName, null, totalQty, wantDate,
                    rAddr, note.isBlank() ? null : note, sku.isBlank() ? null : sku, "team",
                    null,                          // 담당자(물류팀)는 접수 후 지정
                    null, productsJson,
                    rName, rPhone, rAddr, rMsg.isBlank() ? null : rMsg,
                    billing,
                    channel.isBlank() ? null : channel,
                    userId.isBlank() ? null : userId
            ));
            srNo = saved.getSrNo();
            // 요청일(접수한 날) 기준 이름표 — 예: 2026년08월10일_신혜인_김다래
            label = friendlyLabel(
                    saved.getReceivedAt() == null ? null : saved.getReceivedAt().toLocalDate(),
                    requester, rName);
        } catch (Exception e) {
            log.error("[Slack] 요청 저장 실패", e);
            return errorJson("b_team", "저장에 실패했습니다. 잠시 후 다시 시도해 주세요.");
        }

        // 접수 완료 메시지는 응답을 막지 않도록 비동기로 전송
        final String finalSrNo = srNo;
        final String finalLabel = (label == null || label.isBlank()) ? srNo : label;
        final int finalQty = totalQty;
        final LocalDate finalDate = wantDate;
        final String finalSku = sku;
        if (!channel.isBlank()) {
            async(() -> {
                // 상품코드를 입력했으면 이지어드민에서 실시간 가용재고를 조회해 같이 보여줍니다.
                // 조회 실패해도 접수 메시지 자체는 정상 발송합니다 (재고 표시는 부가 정보).
                String stockLine = "";
                if (!finalSku.isBlank()) {
                    try {
                        var stock = ezAdminService.lookup(List.of(finalSku));
                        var line = stock.get(finalSku);
                        if (line != null) {
                            stockLine = line.available() != null
                                    ? String.format("*가용재고*\\n%d개 (현재고 %d)", line.available(), line.stock())
                                    : String.format("*현재고*\\n%d개", line.stock());
                        } else {
                            stockLine = "*재고조회*\\n이지어드민에 없는 코드입니다";
                        }
                    } catch (Exception e) {
                        log.error("[Slack] 재고조회 실패", e);
                    }
                }

                try {
                    var fields = om.createArrayNode();
                    fields.add(field("요청팀", esc(team)));
                    fields.add(field("요청자", esc(requester)));
                    fields.add(field("품목", esc(itemName) + (products.size() > 1 ? " 외 " + (products.size() - 1) + "건" : "")));
                    fields.add(field("수량", String.valueOf(finalQty)));
                    fields.add(field("출고 희망일", finalDate.toString()));
                    fields.add(field("구분", "paid".equals(billing) ? "유상" : "무상"));
                    if (!stockLine.isBlank()) {
                        var f = om.createObjectNode();
                        f.put("type", "mrkdwn");
                        f.put("text", stockLine.replace("\\n", "\n"));
                        fields.add(f);
                    }

                    var section1 = om.createObjectNode();
                    section1.put("type", "section");
                    var text1 = om.createObjectNode();
                    text1.put("type", "mrkdwn");
                    text1.put("text", "*" + finalLabel + "* 출고 요청이 접수되었습니다. :white_check_mark:\n_접수번호 " + finalSrNo + "_");
                    section1.set("text", text1);

                    var section2 = om.createObjectNode();
                    section2.put("type", "section");
                    section2.set("fields", fields);

                    var blocksArr = om.createArrayNode();
                    blocksArr.add(section1);
                    blocksArr.add(section2);

                    postMessage(channel, null, finalSrNo + " 출고 요청이 접수되었습니다.", om.writeValueAsString(blocksArr));
                } catch (Exception e) {
                    log.error("[Slack] 접수 확인 메시지 생성 실패", e);
                }
            });
        }
        log.info("[Slack] {} 접수 완료 (제출자: {}, 발주서 사용: {})", srNo, userName, hasFile);
        return ""; // 빈 문자열 = 정상, 모달 닫힘
    }

    private com.fasterxml.jackson.databind.node.ObjectNode field(String label, String value) {
        var f = om.createObjectNode();
        f.put("type", "mrkdwn");
        f.put("text", "*" + label + "*\n" + value);
        return f;
    }

    private String errorJson(String blockId, String message) {
        try {
            var root = om.createObjectNode();
            root.put("response_action", "errors");
            var errors = om.createObjectNode();
            errors.put(blockId, message);
            root.set("errors", errors);
            return om.writeValueAsString(root);
        } catch (Exception e) {
            return "";
        }
    }

    /** JSON 문자열 안에서 깨지지 않도록 최소한만 이스케이프 */
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", " ").replace("\r", " ");
    }

    // ------------------------------------------------------------------
    // 공통: Slack Web API 호출
    // ------------------------------------------------------------------

    private void call(String method, String jsonBody) {
        if (botToken == null || botToken.isBlank()) {
            log.error("[Slack] BOT_TOKEN 미설정 — {} 호출을 건너뜁니다.", method);
            return;
        }
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(SLACK_API + method))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Authorization", "Bearer " + botToken)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonNode node = om.readTree(res.body());
            if (!node.path("ok").asBoolean(false)) {
                log.error("[Slack] {} 실패: {}", method, res.body());
            }
        } catch (Exception e) {
            log.error("[Slack] {} 호출 오류", method, e);
        }
    }

    /**
     * Slack users.list 로 워크스페이스 직원 목록을 받아 "이름 → SlackID" 주소록에 일괄 저장합니다.
     * (봇 토큰에 users:read 권한이 필요합니다. 없으면 0을 돌려주고 로그만 남깁니다.)
     * 반환값 = 저장(갱신)한 인원 수.
     */
    public int importUsersFromSlack() {
        if (botToken == null || botToken.isBlank()) {
            log.error("[Slack] BOT_TOKEN 미설정 — users.list 건너뜁니다.");
            return 0;
        }
        int count = 0;
        String cursor = "";
        try {
            for (int page = 0; page < 20; page++) {   // 안전상 최대 20페이지
                String url = SLACK_API + "users.list?limit=200"
                        + (cursor.isBlank() ? "" : "&cursor=" + urlEnc(cursor));
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .header("Authorization", "Bearer " + botToken)
                        .GET().build();
                HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                JsonNode root = om.readTree(res.body());
                if (!root.path("ok").asBoolean(false)) {
                    log.error("[Slack] users.list 실패: {}", res.body());
                    break;
                }
                for (JsonNode m : root.path("members")) {
                    if (m.path("is_bot").asBoolean(false) || m.path("deleted").asBoolean(false)) continue;
                    String id = m.path("id").asText("");
                    if (id.isBlank() || "USLACKBOT".equals(id)) continue;
                    JsonNode prof = m.path("profile");
                    String real = prof.path("real_name").asText("");
                    String disp = prof.path("display_name").asText("");
                    boolean saved = false;
                    if (!real.isBlank()) { staffDirectory.remember(real, id, null); saved = true; }
                    if (!disp.isBlank() && !disp.equals(real)) { staffDirectory.remember(disp, id, null); saved = true; }
                    if (saved) count++;
                }
                cursor = root.path("response_metadata").path("next_cursor").asText("");
                if (cursor.isBlank()) break;
            }
        } catch (Exception e) {
            log.error("[Slack] users.list 처리 오류", e);
        }
        log.info("[Slack] 직원 주소록 일괄 저장 완료 — {}명", count);
        return count;
    }

    /** Slack은 3초 안에 응답을 요구하므로, 오래 걸리는 일은 따로 돌립니다. */
    public void async(Runnable task) {
        CompletableFuture.runAsync(task).exceptionally(e -> {
            log.error("[Slack] 비동기 처리 오류", e);
            return null;
        });
    }

    private String text(JsonNode n, String field) {
        return n.hasNonNull(field) ? n.get(field).asText() : null;
    }
    /**
     * [상품별 상태] 주문 안의 특정 상품이 보류/반려/교환요청 되었을 때,
     * Slack으로 접수됐던 요청자에게 어떤 상품이 왜 막혔는지 알려줍니다.
     * (완료/송장 알림과 동일한 방식 — 이 클래스의 esc(), postMessage() 를 그대로 사용)
     *   action: "hold" | "reject" | "exchange"
     *   reason: 대상 상품명 + 사유 문장 (예: "재고부족 반려: 상품A, 상품B")
     */
    public void notifyProductIssue(String channelId, String userId, String srNo, String action, String reason, String label) {
        String title = (label == null || label.isBlank()) ? srNo : label;
        String kind = switch (action == null ? "" : action) {
            case "reject" -> "재고 부족으로 반려";
            case "exchange" -> "교환 요청";
            default -> "보류";
        };
        String emoji = "reject".equals(action) ? ":warning:"
                : "exchange".equals(action) ? ":arrows_counterclockwise:"
                : ":hourglass_flowing_sand:";

        String blocks = ("""
                [
                  {"type":"section","text":{"type":"mrkdwn","text":
                    "*LABEL* 요청 중 일부 상품이 EMOJI *KIND* 되었습니다.\\n\\n*대상 상품 / 사유*\\nREASON\\n\\n_접수번호 SR_NO_\\n\\n확인 후 해당 상품을 수정해서 다시 요청해 주세요."}}
                ]
                """)
                .replace("LABEL", esc(title))
                .replace("SR_NO", esc(srNo))
                .replace("EMOJI", emoji)
                .replace("KIND", kind)
                .replace("REASON", esc(reason == null ? "" : reason));

        String target = (userId != null && !userId.isBlank()) ? userId : channelId;
        if (target == null || target.isBlank()) return;
        postMessage(target, null, title + " 일부 상품 " + kind, blocks);
    }

/**
     * 요청이 '수정' 또는 '취소'되면 물류팀 3명에게 DM으로 알려줍니다.
     * 누가·무엇을·왜 바꿨는지 담아 각자에게 개인 메시지로 보냅니다.
     *   action: "수정" | "취소"
     */
    public void notifyChangeToLogistics(String srNo, String action, String actor, String reason, String label) {
        String title = (label == null || label.isBlank()) ? srNo : label;
        String emoji = "취소".equals(action) ? ":x:" : ":pencil2:";
        String who = (actor == null || actor.isBlank()) ? "(미입력)" : actor;
        String why = (reason == null || reason.isBlank()) ? "-" : reason;
        String blocks = ("""
                [
                  {"type":"section","text":{"type":"mrkdwn","text":
                    "EMOJI *요청 ACTION 알림*\\n*LABEL*\\n_접수번호 SR_NO_\\n\\n*변경자* WHO\\n*사유* WHY"}}
                ]
                """)
                .replace("EMOJI", emoji)
                .replace("ACTION", esc(action))
                .replace("LABEL", esc(title))
                .replace("SR_NO", esc(srNo))
                .replace("WHO", esc(who))
                .replace("WHY", esc(why));
        String fallback = title + " 요청이 " + action + "되었습니다 (변경자: " + who + ")";
        for (String uid : LOGISTICS_USER_IDS) {
            if (uid == null || uid.isBlank()) continue;
            postMessage(uid, null, fallback, blocks);
        }
    }

private String resolveDmChannel(String target) {
        if (target == null || target.isBlank()) return target;
        char c0 = target.charAt(0);
        if (c0 != 'U' && c0 != 'W') return target;
        if (botToken == null || botToken.isBlank()) return target;
        try {
            var body = om.createObjectNode();
            body.put("users", target);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(SLACK_API + "conversations.open"))
                    .timeout(Duration.ofSeconds(8))
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Authorization", "Bearer " + botToken)
                    .POST(HttpRequest.BodyPublishers.ofString(om.writeValueAsString(body), StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonNode node = om.readTree(res.body());
            if (node.path("ok").asBoolean(false)) {
                String id = node.path("channel").path("id").asText("");
                if (!id.isBlank()) return id;
            } else {
                log.error("[Slack] DM 채널 열기 실패: {}", res.body());
            }
        } catch (Exception e) {
            log.error("[Slack] DM 채널 열기 오류", e);
        }
        return target;
    }
}
