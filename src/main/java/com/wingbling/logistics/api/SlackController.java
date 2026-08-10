package com.wingbling.logistics.api;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
/**
 * Slack이 우리 서버로 보내는 요청을 받는 창구.
 *
 *  POST /slack/events        — 봇이 태그됐을 때 (Event Subscriptions)
 *  POST /slack/interactions  — 버튼 클릭 / 폼 제출 (Interactivity & Shortcuts)
 *
 * Slack은 3초 안에 200 응답을 요구합니다.
 * 그래서 무거운 일은 async로 넘기고 응답부터 돌려줍니다.
 */
@RestController
@RequestMapping("/slack")
@RequiredArgsConstructor
public class SlackController {
    private static final Logger log = LoggerFactory.getLogger(SlackController.class);
    private final SlackService slack;
    private final ObjectMapper om = new ObjectMapper();
    // ------------------------------------------------------------------
    // Event Subscriptions
    // ------------------------------------------------------------------
    @PostMapping(value = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> events(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Slack-Request-Timestamp", required = false) String ts,
            @RequestHeader(value = "X-Slack-Signature", required = false) String sig,
            @RequestHeader(value = "X-Slack-Retry-Num", required = false) String retryNum
    ) {
        JsonNode root;
        try {
            root = om.readTree(rawBody);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("invalid json");
        }
        // 1) URL 검증 — Slack 앱 설정에서 주소를 등록할 때 딱 한 번 옵니다.
        //    서명 검증보다 먼저 처리해야 등록이 됩니다.
        if ("url_verification".equals(root.path("type").asText())) {
            String challenge = root.path("challenge").asText("");
            log.info("[Slack] URL 검증 요청 수신");
            return ResponseEntity.ok(challenge);
        }
        // 2) 진짜 Slack이 보낸 요청인지 확인
        if (!slack.verify(ts, sig, rawBody)) {
            log.warn("[Slack] 서명 검증 실패 — 요청 거부");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("bad signature");
        }
        // 3) 재전송은 무시 (같은 답글이 여러 번 달리는 것 방지)
        if (retryNum != null) {
            log.info("[Slack] 재전송 요청 무시 (retry={})", retryNum);
            return ResponseEntity.ok("ok");
        }
        JsonNode event = root.path("event");
        String eventType = event.path("type").asText("");
        // 봇 자신이 쓴 글에는 반응하지 않음 (무한 반복 방지)
        boolean isBot = event.hasNonNull("bot_id") || "bot_message".equals(event.path("subtype").asText());
        if ("app_mention".equals(eventType) && !isBot) {
            slack.async(() -> slack.handleAppMention(event));
        }
        // 봇에게 직접 보낸 개인 DM(1:1 대화)도 채널 멘션과 똑같이 '요청서 작성' 버튼으로 응답합니다.
        // (사람이 직접 친 메시지만 — 편집/삭제/봇 메시지는 subtype·bot_id로 걸러 무한 반복을 막습니다.)
        boolean isDirectMessage = "message".equals(eventType)
                && "im".equals(event.path("channel_type").asText())
                && event.path("subtype").asText("").isBlank();
        if (isDirectMessage && !isBot) {
            slack.async(() -> slack.handleDirectMessage(event));
        }
        return ResponseEntity.ok("ok");
    }
    // ------------------------------------------------------------------
    // Interactivity (버튼 클릭 / 모달 제출)
    // ------------------------------------------------------------------
    @PostMapping(value = "/interactions", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> interactions(
            @RequestBody String rawBody,
            @RequestHeader(value = "X-Slack-Request-Timestamp", required = false) String ts,
            @RequestHeader(value = "X-Slack-Signature", required = false) String sig
    ) {
        if (!slack.verify(ts, sig, rawBody)) {
            log.warn("[Slack] 서명 검증 실패 — interaction 거부");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("bad signature");
        }
        // payload=<URL 인코딩된 JSON> 형태로 옵니다.
        String json = null;
        for (String pair : rawBody.split("&")) {
            int i = pair.indexOf('=');
            if (i > 0 && "payload".equals(pair.substring(0, i))) {
                json = URLDecoder.decode(pair.substring(i + 1), StandardCharsets.UTF_8);
                break;
            }
        }
        if (json == null) return ResponseEntity.ok("");
        JsonNode payload;
        try {
            payload = om.readTree(json);
        } catch (Exception e) {
            return ResponseEntity.ok("");
        }
        String type = payload.path("type").asText("");
        // 버튼 클릭 → 모달 열기
        // trigger_id는 3초 안에 써야 해서 여기서는 기다리지 않고 바로 띄웁니다.
        if ("block_actions".equals(type)) {
            String actionId = payload.path("actions").path(0).path("action_id").asText("");
            if ("open_request_form".equals(actionId)) {
                String triggerId = payload.path("trigger_id").asText("");
                String channelId = payload.path("channel").path("id").asText("");
                slack.async(() -> slack.openRequestModal(triggerId, channelId));
            }
            return ResponseEntity.ok("");
        }
        // 모달 제출 → DB 저장
        // 발주서 파일을 내려받아 읽는 과정이 있어 동기로 처리합니다 (Slack 제한: 3초 이내 응답).
        // 오류가 있으면 Slack이 모달에 바로 표시할 수 있도록 JSON을 그대로 돌려줍니다.
        if ("view_submission".equals(type)) {
            String result = slack.handleViewSubmissionSync(payload);
            if (result.isEmpty()) {
                return ResponseEntity.ok("");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        }
        return ResponseEntity.ok("");
    }
    /** 배포 확인용 — 브라우저로 열어서 살아있는지 볼 수 있습니다. */
    @GetMapping("/health")
    public String health() {
        return "slack endpoint alive";
    }
}
