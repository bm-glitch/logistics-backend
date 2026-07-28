package com.wingbling.logistics.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

/**
 * 이지어드민 재고조회(확장) API 연동.
 *
 * 이지어드민이 실제로 주는 값은 "현재고(stock)"와 "접수/송장 수량(ready_trans_stock)"
 * 두 가지뿐입니다. "미처리"가 별도로 나뉘어 오지 않으므로,
 * 가용재고 = 현재고 - 접수/송장 수량 으로 계산합니다.
 *
 * 상품코드(SKU) 기준으로만 조회가 가능합니다. 상품명으로는 조회되지 않습니다.
 */
@Service
public class EzAdminService {

    private static final Logger log = LoggerFactory.getLogger(EzAdminService.class);
    private static final String BASE_URL = "https://api2.ezadmin.co.kr/function.php";
    private static final int BATCH_SIZE = 100; // 이지어드민 1회 최대 조회 건수

    private final ObjectMapper om = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${ezadmin.partner-key:}")
    private String partnerKey;

    @Value("${ezadmin.domain-key:}")
    private String domainKey;

    /** 재고 조회 결과 한 건. available은 계산값(현재고 - 접수/송장), 계산 불가하면 null. */
    public record StockLine(String productId, int stock, Integer readyTransStock, Integer available) {}

    /**
     * 상품코드 목록으로 재고를 조회합니다. 결과에 없는 코드는 맵에서 빠집니다(이지어드민에 없는 코드).
     * 100개 넘으면 자동으로 나눠서 여러 번 호출합니다 (이지어드민 제한).
     */
    public Map<String, StockLine> lookup(List<String> productCodes) {
        Map<String, StockLine> result = new LinkedHashMap<>();
        if (productCodes == null || productCodes.isEmpty()) return result;
        if (partnerKey == null || partnerKey.isBlank() || domainKey == null || domainKey.isBlank()) {
            log.error("[EzAdmin] 인증키(EZADMIN_PARTNER_KEY/EZADMIN_DOMAIN_KEY) 미설정 — 조회를 건너뜁니다.");
            return result;
        }

        List<String> distinct = productCodes.stream().filter(s -> s != null && !s.isBlank())
                .map(String::trim).distinct().toList();

        for (int i = 0; i < distinct.size(); i += BATCH_SIZE) {
            List<String> batch = distinct.subList(i, Math.min(i + BATCH_SIZE, distinct.size()));
            result.putAll(callOnce(batch));
        }
        return result;
    }

    private Map<String, StockLine> callOnce(List<String> batch) {
        Map<String, StockLine> out = new LinkedHashMap<>();
        try {
            String ids = String.join(",", batch);
            String query = "action=get_stock_info_ext"
                    + "&partner_key=" + enc(partnerKey)
                    + "&domain_key=" + enc(domainKey)
                    + "&type=product_id"
                    + "&ids=" + enc(ids)
                    + "&include_ready_trans=1";

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "?" + query))
                    .timeout(Duration.ofSeconds(8))
                    .GET().build();

            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonNode root = om.readTree(res.body());

            if (root.path("error").asInt(-1) != 0) {
                log.error("[EzAdmin] 재고조회 실패: {}", root.path("msg").asText(""));
                return out;
            }

            JsonNode data = root.path("data");
            var fields = data.fields();
            while (fields.hasNext()) {
                var entry = fields.next();
                JsonNode v = entry.getValue();
                int stock = v.path("stock").asInt(0);
                Integer ready = v.has("ready_trans_stock") ? v.path("ready_trans_stock").asInt(0) : null;
                Integer available = ready != null ? (stock - ready) : null;
                out.put(entry.getKey(), new StockLine(
                        v.path("product_id").asText(entry.getKey()), stock, ready, available));
            }
        } catch (Exception e) {
            log.error("[EzAdmin] 재고조회 호출 오류", e);
        }
        return out;
    }

    private String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}