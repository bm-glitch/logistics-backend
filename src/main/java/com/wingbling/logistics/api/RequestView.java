package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.ShipmentRequest;

import java.time.format.DateTimeFormatter;

/** 프론트(출고요청 대장 대시보드)가 기대하는 필드 이름에 맞춘 응답 뷰 */
public record RequestView(
        String sr, String recv, String team, String by,
        String item, String opt, Integer qty, String want, String to,
        String note, String status, String scope, String assignee,
        String sku, Integer phys, Integer inv, Integer pend,
        String hold
) {
    private static final DateTimeFormatter RECV_FMT = DateTimeFormatter.ofPattern("MM-dd HH:mm");

    public static RequestView from(ShipmentRequest r) {
        return new RequestView(
                r.getSrNo(),
                r.getReceivedAt().format(RECV_FMT),
                r.getRequestTeam(),
                r.getRequester(),
                r.getItemName(),
                r.getOptionValue() == null ? "—" : r.getOptionValue(),
                r.getQuantity(),
                r.getWantDate().toString(),
                r.getReceivePlace(),
                r.getNote(),
                r.getStatus(),
                r.getScope(),
                r.getAssignee(),
                r.getSku(),
                r.getStockPhys(),
                r.getStockInvoice(),
                r.getStockPending(),
                r.getHoldReason()
        );
    }
}
