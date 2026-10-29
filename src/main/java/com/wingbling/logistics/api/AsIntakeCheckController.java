package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.AsIntakeCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * A/S 입고 이중체크 화면 전용 API. 비밀번호 확인 → 목록 조회/새로고침 → 스캔 체크 순서로 씁니다.
 */
@RestController
@RequestMapping("/api/as-intake-checks")
@RequiredArgsConstructor
public class AsIntakeCheckController {

    private final AsIntakeCheckService service;

    @GetMapping
    public List<AsIntakeCheck> list() {
        return service.list();
    }

    /** 화면에서 고른 기간(보름~한달 등)의 입고/반품입고 이력을 이지어드민에서 다시 받아옵니다. */
    @PostMapping("/sync")
    public SyncResult sync(@RequestParam String start, @RequestParam String end) {
        int added = service.syncFromEzAdmin(LocalDate.parse(start), LocalDate.parse(end));
        return new SyncResult(added, service.list().size());
    }

    @PatchMapping("/check")
    public List<AsIntakeCheck> check(@RequestBody CheckDto body) {
        return service.markChecked(body.ids(), body.checkedBy());
    }

    @PatchMapping("/{id}/uncheck")
    public AsIntakeCheck uncheck(@PathVariable Long id) {
        return service.unmark(id);
    }

    public record SyncResult(int added, int total) {}
    public record CheckDto(List<Long> ids, String checkedBy) {}
}
