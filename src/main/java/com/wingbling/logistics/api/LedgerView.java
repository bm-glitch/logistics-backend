package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.ShipmentRequest;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 팀원의 "업무 요청 대장부" 화면(REQUESTS 배열 형태)에 맞춘 응답 뷰.
 * 우리 v4 프로토타입은 RequestView를 그대로 쓰고, 이 뷰는 팀원 화면 전용.
 */
public record LedgerView(
        String requestId, String receivedAt, String requestDate,
        String team, String requester, String assignee, String content,
        List<String> channels, String channelOther,
        String productsJson,          // 클라이언트에서 JSON.parse 해서 사용
        Receiver receiver,
        String desiredDate, String billing, String note, String status,
        String completedAt
) {
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public record Receiver(String name, String phone, String address, String message) {}

    // 팀원 화면은 pending/in_progress/done 3단계만 앎. 보류는 pending으로 접어서 안 잃어버리게 함.
    private static String mapStatus(String korean) {
        return switch (korean) {
            case "진행중" -> "in_progress";
            case "완료" -> "done";
            default -> "pending"; // 대기, 보류
        };
    }

    public static LedgerView from(ShipmentRequest r) {
        List<String> channelList = (r.getChannels() == null || r.getChannels().isBlank())
                ? List.of()
                : Arrays.asList(r.getChannels().split(","));

        return new LedgerView(
                r.getSrNo(),
                r.getReceivedAt().format(TS_FMT),
                r.getReceivedAt().toLocalDate().toString(),
                r.getRequestTeam(),
                r.getRequester(),
                r.getAssignee(),
                r.getNote(),                       // content로 매핑 (요청내용)
                channelList,
                null,                               // channelOther는 channels 문자열에 이미 포함
                r.getProductsJson() == null ? "[]" : r.getProductsJson(),
                new Receiver(r.getReceiverName(), r.getReceiverPhone(), r.getReceiverAddress(), r.getReceiverMessage()),
                r.getWantDate().toString(),
                r.getBillingType() == null ? "free" : r.getBillingType(),
                r.getNote(),
                mapStatus(r.getStatus()),
                r.getCompletedAt() == null ? "" : r.getCompletedAt().format(TS_FMT)
        );
    }
}