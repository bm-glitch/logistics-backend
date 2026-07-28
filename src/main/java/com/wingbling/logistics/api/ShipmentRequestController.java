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
        // TODO(내일): 여기서 실제 Slack DM 발송 호출 붙이기
        return RequestView.from(service.markNotified(sr));
    }

    public record StatusUpdateDto(String status) {}
    public record HoldDto(String reason) {}
}
