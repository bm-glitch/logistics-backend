package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.MatchMemory;
import com.wingbling.logistics.domain.MatchMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 상품 매칭 학습(기억) 창구.
 *  - GET  /api/match-memory        : 저장된 매칭 전체 (대시보드가 시작 시 불러와 자동매칭에 씀)
 *  - POST /api/match-memory        : 관리자가 수동으로 확정한 매칭을 저장/갱신
 */
@RestController
@RequestMapping("/api/match-memory")
@RequiredArgsConstructor
public class MatchMemoryController {

    private final MatchMemoryRepository repo;

    @GetMapping
    public List<Map<String, Object>> list() {
        return repo.findAll().stream().map(m -> {
            Map<String, Object> r = new LinkedHashMap<>();
            r.put("aliasKey", m.getAliasKey());
            r.put("productId", m.getProductId());
            r.put("option", m.getOptionValue());
            r.put("name", m.getProductName());
            return r;
        }).toList();
    }

    @PostMapping
    @Transactional
    public Map<String, Object> upsert(@RequestBody SaveDto body) {
        if (body.aliasKey() == null || body.aliasKey().isBlank()
                || body.productId() == null || body.productId().isBlank()) {
            return Map.of("ok", false, "error", "aliasKey/productId 필수");
        }
        String key = body.aliasKey().trim();
        MatchMemory m = repo.findByAliasKey(key).orElseGet(MatchMemory::new);
        m.setAliasKey(key);
        m.setProductId(body.productId().trim());
        m.setOptionValue(body.option());
        m.setProductName(body.name());
        m.setSourceName(body.sourceName());
        m.setUpdatedBy(body.updatedBy());
        m.setUpdatedAt(LocalDateTime.now());
        repo.save(m);
        return Map.of("ok", true);
    }

    public record SaveDto(String aliasKey, String productId, String option,
                          String name, String sourceName, String updatedBy) {}
}
