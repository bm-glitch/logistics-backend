package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.ShipmentRequest;
import com.wingbling.logistics.domain.ShipmentRequestRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class ShipmentRequestController {

    private final ShipmentRequestService service;
    private final ShipmentRequestRepository repo;
    private final SlackService slackService;

    /** 접수 창구. */
    @PostMapping
    public RequestView create(@Valid @RequestBody CreateRequestDto dto) {
        return RequestView.from(service.create(dto));
    }

    /** 대시보드가 화면 그릴 때 호출. scope로 통합/개인 필터링. */
    @GetMapping
    public List<RequestView> list(@RequestParam(required = false) String scope) {
        List<ShipmentRequest> rows = (scope == null)
                ? repo.findAll()
                : repo.findByScopeOrderByWantDateAsc(scope);
        return rows.stream().map(RequestView::from).toList();
    }

    /** 팀원의 "업무 요청 대장부" 화면 전용 — REQUESTS 배열과 같은 필드 구조로 응답 */
    @GetMapping("/ledger")
    public List<LedgerView> ledger() {
        return repo.findAll().stream().map(LedgerView::from).toList();
    }

    /**
     * "물류 대장" 화면 전용 — 취소된 건은 물류팀이 처리할 게 없으니 제외하고,
     * 출고요청만 보여줍니다(재고확보/안전재고는 출고 대상이 아니므로 물류 대장에서 제외).
     */
    @GetMapping("/logistics-view")
    public List<LogisticsRowView> logisticsView() {
        return repo.findAll().stream()
                .filter(r -> !"취소".equals(r.getStatus()))
                .filter(r -> r.getRequestType() == null || "출고요청".equals(r.getRequestType()))
                .map(LogisticsRowView::from)
                .toList();
    }

    @PatchMapping("/{sr}/status")
    public RequestView updateStatus(@PathVariable String sr, @RequestBody StatusUpdateDto body) {
        return RequestView.from(service.updateStatus(sr, body.status()));
    }

    @PatchMapping("/{sr}/hold")
    public RequestView hold(@PathVariable String sr, @RequestBody HoldDto body) {
        return RequestView.from(service.hold(sr, body.reason()));
    }

    /**
     * [상품별 상태] 주문 안의 특정 상품(들)만 보류/반려/교환요청으로 표시합니다.
     * 상품별 독립 상태를 products_json 에 저장하고, Slack으로 접수된 요청이면
     * 어떤 상품이 왜 막혔는지 요청자에게 알림을 보냅니다. (action="clear"는 알림 없음)
     */
    @PatchMapping("/{sr}/products/status")
    public RequestView updateProductStatus(@PathVariable String sr, @RequestBody ProductStatusDto body) {
        ShipmentRequest r = service.updateProductStatus(sr, body.action(), body.reason(), body.indexes());
        boolean notify = body.action() != null && !"clear".equals(body.action());
        if (notify && r.getSlackChannelId() != null && !r.getSlackChannelId().isBlank()) {
            slackService.async(() -> slackService.notifyProductIssue(
                    r.getSlackChannelId(), r.getSlackUserId(), r.getSrNo(), body.action(), body.reason(), slackLabel(r)));
        }
        return RequestView.from(r);
    }

    public record ProductStatusDto(String action, String reason, List<Integer> indexes) {}

    /** [요청유형] 요청의 유형(출고요청/재고확보/안전재고)을 지정합니다. (안전재고 요청서 접수 직후 호출) */
    @PatchMapping("/{sr}/type")
    public RequestView setType(@PathVariable String sr, @RequestBody TypeDto body) {
        return RequestView.from(service.changeRequestType(sr, body.type()));
    }

    public record TypeDto(String type) {}

    @PostMapping("/{sr}/notify")
    public RequestView notify(@PathVariable String sr) {
        ShipmentRequest r = service.markNotified(sr);
        if (r.getSlackChannelId() != null && !r.getSlackChannelId().isBlank()) {
            slackService.async(() -> slackService.notifyCompletion(
                    r.getSlackChannelId(), r.getSlackUserId(), r.getSrNo(), slackLabel(r)));
        }
        return RequestView.from(r);
    }

    /** 요청자에게 보일 이름표(요청일_요청자_수령인)를 만듭니다. 예: 2026년08월10일_신혜인_김다래 */
    private String slackLabel(ShipmentRequest r) {
        return slackService.friendlyLabel(
                r.getReceivedAt() == null ? null : r.getReceivedAt().toLocalDate(),
                r.getRequester(), r.getReceiverName());
    }

    /** 요청자가 내용을 수정. 이미 진행중/완료였다면 물류팀 알림이 자동으로 남습니다. */
    @PatchMapping("/{sr}")
    public RequestView edit(@PathVariable String sr, @Valid @RequestBody CreateRequestDto dto) {
        return RequestView.from(service.edit(sr, dto));
    }

    /** 요청자가 취소. 상태를 '취소'로 바꿀 뿐 데이터는 남깁니다. */
    @PatchMapping("/{sr}/cancel")
    public RequestView cancel(@PathVariable String sr) {
        return RequestView.from(service.cancel(sr));
    }

    /** 물류팀이 확인해야 할, 이미 진행중/완료인 건에 대한 요청자 변경 목록 */
    @GetMapping("/alerts")
    public List<LedgerView> alerts() {
        return service.findPendingAlerts().stream().map(LedgerView::from).toList();
    }

    /** 물류팀이 알림을 확인(닫기) */
    @PostMapping("/{sr}/alerts/ack")
    public void ackAlert(@PathVariable String sr) {
        service.acknowledgeAlert(sr);
    }

    /**
     * 물류팀이 택배사/송장번호를 등록. 등록 자체는 알림을 보내지 않습니다.
     * 미리 송장을 뽑아두고 출고는 나중에 하는 경우가 있어, 알림은 별도로
     * "/{sr}/notify-tracking" 을 호출해야만 나갑니다.
     */
    @PatchMapping("/{sr}/tracking")
    public RequestView registerTracking(@PathVariable String sr, @RequestBody TrackingDto body) {
        ShipmentRequest r = service.registerTracking(sr, body.carrier(), body.trackingNo());
        return RequestView.from(r);
    }

    /**
     * 이미 등록된 송장 정보를 요청자에게 알림. 등록과 알림을 분리해서,
     * 물류팀이 준비된 시점에 직접 눌러야만 Slack 알림이 나갑니다.
     */
    @PostMapping("/{sr}/notify-tracking")
    public RequestView notifyTracking(@PathVariable String sr) {
        ShipmentRequest r = service.markTrackingNotified(sr);
        if (r.getSlackChannelId() != null && !r.getSlackChannelId().isBlank()) {
            slackService.async(() -> slackService.notifyTracking(
                    r.getSlackChannelId(), r.getSlackUserId(), r.getSrNo(), r.getCarrier(), r.getTrackingNo(), slackLabel(r)));
        }
        return RequestView.from(r);
    }

    public record TrackingDto(String carrier, String trackingNo) {}

    /**
     * 이지어드민 관리번호(seq)를 연결. 이후 백그라운드 작업이 주기적으로 확인해서,
     * 송장이 등록되면 자동으로 채우고 Slack 알림까지 보냅니다. (수기 입력 불필요)
     */
    @PatchMapping("/{sr}/link-order")
    public RequestView linkOrder(@PathVariable String sr, @RequestBody LinkOrderDto body) {
        return RequestView.from(service.linkEzAdminOrder(sr, body.seq()));
    }

    public record LinkOrderDto(String seq) {}

    public record StatusUpdateDto(String status) {}
    public record HoldDto(String reason) {}
}
