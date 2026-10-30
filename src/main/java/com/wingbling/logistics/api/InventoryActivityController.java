package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.InventoryActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/** 재고 작업 현황 화면 전용 API. 조회 → (필요시) 이지어드민에서 다시 받아오기 순서로 씁니다. */
@RestController
@RequestMapping("/api/inventory-activity")
@RequiredArgsConstructor
public class InventoryActivityController {

    private final InventoryActivityService service;

    @GetMapping
    public List<InventoryActivity> list() {
        return service.list();
    }

    /** 화면에서 고른 기간의 전체 재고 변동 이력을 이지어드민에서 다시 받아옵니다. */
    @PostMapping("/sync")
    public SyncResult sync(@RequestParam String start, @RequestParam String end) {
        int added = service.syncFromEzAdmin(LocalDate.parse(start), LocalDate.parse(end));
        return new SyncResult(added, service.list().size());
    }

    public record SyncResult(int added, int total) {}
}
