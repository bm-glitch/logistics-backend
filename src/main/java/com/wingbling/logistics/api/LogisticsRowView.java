package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.ShipmentRequest;

import java.time.format.DateTimeFormatter;

/**
 * "물류 대장" 화면(출고요청 대장 프로토타입) 전용 응답 뷰.
 * 그 화면의 목업 데이터(DATA 배열)와 필드 이름을 그대로 맞췄습니다.
 *
 * 재고(phys/inv/pend)는 여기서 채우지 않습니다 — 화면이 열릴 때
 * /api/stock 으로 sku 기준 실시간 조회해서 화면에서 직접 계산합니다.
 * (pend=미처리는 이지어드민이 별도로 안 주는 값이라 항상 0으로 둡니다.)
 */
public record LogisticsRowView(
        String sr, String sku,
        int phys, int inv, int pend,
        String recv, String team, String by, String item, String opt,
        int qty, String want, String to, String note,
        String status, String hold, String scope, String assignee,
        boolean sent,
        String carrier, String trackingNo, boolean trackingNotified, String ezadminSeq,
        String requestType
) {
    private static final DateTimeFormatter RECV_FMT = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    public static LogisticsRowView from(ShipmentRequest r) {
        return new LogisticsRowView(
                r.getSrNo(),
                r.getSku() == null ? "" : r.getSku(),
                0, 0, 0,
                r.getReceivedAt().format(RECV_FMT),
                r.getRequestTeam(),
                r.getRequester(),
                r.getItemName(),
                (r.getOptionValue() == null || r.getOptionValue().isBlank()) ? "—" : r.getOptionValue(),
                r.getQuantity(),
                r.getWantDate().toString(),
                r.getReceivePlace() == null ? "" : r.getReceivePlace(),
                r.getNote() == null ? "" : r.getNote(),
                r.getStatus(),
                r.getHoldReason(),
                r.getScope() == null ? "team" : r.getScope(),
                r.getAssignee(),
                r.getNotifiedAt() != null,
                r.getCarrier(),
                r.getTrackingNo(),
                r.getTrackingNotifiedAt() != null,
                r.getEzadminSeq(),
                r.getRequestType() == null ? "출고요청" : r.getRequestType()
        );
    }
}
