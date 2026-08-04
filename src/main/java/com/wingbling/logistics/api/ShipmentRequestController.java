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

    /**
     * 접수 창구. 지금은 Postman/curl로 수동 테스트.
     * 내일 대표님 Slack 봇 형식 확인되면, 이 엔드포인트를 그대로 두거나
     * 형식이 다르면 변환 계층만 하나 앞에 추가하면 됨.
     */
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

    /** "물류 대장" 화면 전용 — 취소된 건은 물류팀이 처리할 게 없으니 제외 */
    @GetMapping("/logistics-view")
    public List<LogisticsRowView> logisticsView() {
        return repo.findAll().stream()
                .filter(r -> !"취소".equals(r.getStatus()))
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

    @PostMapping("/{sr}/notify")
    public RequestView notify(@PathVariable String sr) {
        ShipmentRequest r = service.markNotified(sr);
        if (r.getSlackChannelId() != null && !r.getSlackChannelId().isBlank()) {
            slackService.async(() -> slackService.notifyCompletion(
                    r.getSlackChannelId(), r.getSlackUserId(), r.getSrNo()));
        }
        return RequestView.from(r);
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
     * 물류팀이 택배사/송장번호를 수기로 등록.
     * Slack으로 접수된 요청이면, 등록 즉시 요청자에게 Slack 알림을 보냅니다.
     */
    @PatchMapping("/{sr}/tracking")
    public RequestView registerTracking(@PathVariable String sr, @RequestBody TrackingDto body) {
        ShipmentRequest r = service.registerTracking(sr, body.carrier(), body.trackingNo());
        if (r.getSlackChannelId() != null && !r.getSlackChannelId().isBlank()) {
            slackService.async(() -> slackService.notifyTracking(
                    r.getSlackChannelId(), r.getSlackUserId(), r.getSrNo(), r.getCarrier(), r.getTrackingNo()));
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