package com.wingbling.logistics.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}