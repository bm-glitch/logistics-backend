package com.wingbling.logistics.api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * 이지어드민 실시간 재고 조회 창구.
 *
 * 예시: GET /api/stock?codes=S00011,S00012
 * 응답: {"S00011":{"stock":225,"readyTransStock":10,"available":215}, ...}
 *
 * 이지어드민에 없는 코드는 결과에서 빠집니다(오류 아님 — "코드 없음"으로 처리).
 */
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {
    private final EzAdminService ezAdminService;
    @GetMapping
    public Map<String, Object> get(@RequestParam String codes) {
        List<String> list = List.of(codes.split(","));
        var found = ezAdminService.lookup(list);
        Map<String, Object> body = new LinkedHashMap<>();
        found.forEach((code, line) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("stock", line.stock());
            row.put("readyTransStock", line.readyTransStock());
            row.put("available", line.available());
            body.put(code, row);
        });
        return body;
    }
    /**
     * 상품코드/상품명/바코드로 찾는 검색창 전용.
     * 예시: GET /api/stock/search?q=주얼패치
     * 응답: [{"productId":"S51348","name":"...","stock":1174,"available":1154}, ...]
     * (이지어드민은 상품명 부분검색을 지원하지 않아, 전체 목록을 캐싱해두고 여기서 직접 찾습니다.)
     *
     * withStock=false 로 호출하면 상품 이름·코드만 즉시 돌려주고 재고는 비웁니다.
     * (2단계 표시용 — 프론트가 이름을 먼저 띄우고, 재고는 /api/stock 으로 따로 채웁니다.)
     */
    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam String q,
                                            @RequestParam(required = false, defaultValue = "true") boolean withStock) {
        var candidates = ezAdminService.searchCatalog(q);
        if (candidates.isEmpty()) return List.of();
        Map<String, EzAdminService.StockLine> stock = withStock
                ? ezAdminService.lookup(candidates.stream().map(EzAdminService.CatalogEntry::productId).toList())
                : Map.of();
        return candidates.stream().map(c -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("productId", c.productId());
            row.put("name", c.name());
            row.put("option", c.option());   // 옵션 분리 — 요청서 옵션란 자동 채우기용
            var s = stock.get(c.productId());
            row.put("stock", s == null ? null : s.stock());
            row.put("available", s == null ? null : s.available());
            return row;
        }).toList();
    }
    /** 검색창 옆 '새로고침' 버튼 — 20분 기다리지 않고 지금 바로 상품 목록을 다시 받아옵니다. */
    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        int count = ezAdminService.forceRefreshCatalog();
        return Map.of("count", count);
    }
}
