package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.StockWatch;
import com.wingbling.logistics.domain.StockWatchRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 안전재고 임계치 관리 창구 — 대시보드의 "재고 감시" 화면이 이 API로 등록·조회·수정·삭제하고,
 * "지금 확인" 버튼으로 스케줄(09시/14시)을 기다리지 않고 즉시 체크를 돌릴 수 있습니다.
 */
@RestController
@RequestMapping("/api/stock-watches")
@RequiredArgsConstructor
@Transactional
public class StockWatchController {

    private final StockWatchRepository repo;
    private final StockWatchService stockWatchService;

    @GetMapping
    public List<StockWatch> list() {
        return repo.findAll(org.springframework.data.domain.Sort.by("productName"));
    }

    @PostMapping
    public StockWatch create(@RequestBody SaveDto body) {
        StockWatch w = new StockWatch();
        apply(w, body);
        return repo.save(w);
    }

    @PutMapping("/{id}")
    public StockWatch update(@PathVariable Long id, @RequestBody SaveDto body) {
        StockWatch w = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("감시 상품을 찾을 수 없어요: " + id));
        apply(w, body);
        return repo.save(w);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    @PostMapping("/run-check")
    public List<StockWatch> runCheckNow() {
        stockWatchService.runCheck();
        return repo.findAll(org.springframework.data.domain.Sort.by("productName"));
    }

    private void apply(StockWatch w, SaveDto body) {
        w.setProductCode(body.productCode() == null ? "" : body.productCode().trim());
        w.setProductName(body.productName());
        w.setThresholdQty(body.thresholdQty() == null ? 0 : body.thresholdQty());
    }

    public record SaveDto(String productCode, String productName, Integer thresholdQty) {}
}
