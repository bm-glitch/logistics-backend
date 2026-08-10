package com.wingbling.logistics.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
    // 우리 서버는 해외(Railway EU)에서 돌아가므로 이지어드민 '국외 호출 서버'를 사용합니다.
    // (국내 서버 https://api2.ezadmin.co.kr 는 해외에서 호출 시 연결이 차단됩니다.)
    private static final String BASE_URL = "https://global-api2.ezadmin.co.kr/function.php";
    private static final int BATCH_SIZE = 100; // 이지어드민 1회 최대 조회 건수

    private final ObjectMapper om = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
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
                    .timeout(Duration.ofSeconds(15)) // [개선] 국외 서버 여유 시간 (실시간 재고조회)
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

    // ------------------------------------------------------------------
    // 관리번호(seq)로 주문을 조회해서, 송장번호가 등록됐는지 확인합니다.
    // ------------------------------------------------------------------

    /** 조회 결과 — 아직 송장이 없으면 transNo가 null입니다. */
    public record OrderTracking(String seq, String transCorp, String transNo, String status) {}

    /**
     * 관리번호로 주문을 찾아 송장 등록 여부를 확인합니다.
     * 조회 기간은 최근 90일로 고정합니다 — 이지어드민이 3개월 이전 자료는 조회를 막기 때문입니다.
     */
    public OrderTracking checkOrderTracking(String seq) {
        try {
            String query = "action=get_order_info"
                    + "&partner_key=" + enc(partnerKey)
                    + "&domain_key=" + enc(domainKey)
                    + "&date_type=order_date"
                    + "&start_date=" + java.time.LocalDate.now().minusDays(90)
                    + "&end_date=" + java.time.LocalDate.now()
                    + "&seq=" + enc(seq);
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "?" + query))
                    .timeout(Duration.ofSeconds(10))
                    .GET().build();
            HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            JsonNode root = om.readTree(res.body());
            if (root.path("error").asInt(-1) != 0) {
                log.error("[EzAdmin] 주문조회 실패(seq={}): {}", seq, root.path("msg").asText(""));
                return null;
            }
            JsonNode data = root.path("data");
            if (!data.isArray() || data.isEmpty()) return null;
            JsonNode order = data.get(0); // seq로 조회했으니 한 건만 옴
            String transNo = order.path("trans_no").asText("");
            String transCorp = order.path("trans_corp").asText("");
            String status = order.path("status").asText("");
            return new OrderTracking(seq, transCorp.isBlank() ? null : transCorp,
                    transNo.isBlank() ? null : transNo, status);
        } catch (Exception e) {
            log.error("[EzAdmin] 주문조회 오류(seq={})", seq, e);
            return null;
        }
    }

    // ------------------------------------------------------------------
    // 상품명/키워드 검색 — 이지어드민은 부분(포함) 검색을 지원하지 않으므로,
    // 전체 상품 목록을 주기적으로 받아와 우리 서버 메모리에 캐싱한 뒤
    // 그 안에서 직접 "포함" 검색을 합니다.
    // ------------------------------------------------------------------

    /** 검색 결과 한 줄 (재고는 아직 안 채워짐 — 검색 후 최종 후보만 재고를 별도 조회) */
    public record CatalogEntry(String productId, String name, String barcode) {}

    private volatile List<CatalogEntry> catalogCache = new ArrayList<>();
    private volatile long catalogLoadedAt = 0L;
    // [개선] 캐시 유지시간 20분 → 4시간 (재배포 후 잦은 재적재 방지)
    private static final long CATALOG_TTL_MS = 4L * 60 * 60 * 1000;

    /**
     * [개선] 서버가 완전히 뜬 직후, 백그라운드로 상품 카탈로그를 미리 받아 둡니다(워밍).
     * 이렇게 하면 재배포 직후 "첫 검색자"가 2만 건 로딩을 기다리던 문제(실측 47분)를 없앨 수 있어요.
     * 기동을 막지 않도록 별도 스레드에서 실행하고, 실패해도 서버는 정상 동작합니다.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmCatalogOnStartup() {
        Thread t = new Thread(() -> {
            try {
                log.info("[EzAdmin] 기동 후 상품 카탈로그 미리 적재 시작…");
                refreshCatalogIfStale();
            } catch (Exception e) {
                log.error("[EzAdmin] 기동 시 카탈로그 워밍 실패 — 첫 검색 때 다시 시도합니다.", e);
            }
        }, "ezadmin-cache-warm");
        t.setDaemon(true);
        t.start();
    }

    /** 검색 버튼 옆 '새로고침' — 20분(현재 4시간) 기다리지 않고 지금 바로 캐시를 다시 받아옵니다. */
    public synchronized int forceRefreshCatalog() {
        catalogLoadedAt = 0L;
        refreshCatalogIfStale();
        return catalogCache.size();
    }

    /**
     * 물류팀 내부 관리 전용 상품 — 이 문구로 시작하는 상품명은
     * 타 부서의 재고 검색 결과에는 노출하지 않습니다.
     * (물류팀이 SKU를 직접 알고 조회하는 경우는 영향받지 않습니다.)
     */
    private static final String[] SEARCH_EXCLUDED_PREFIXES = {
            "네이버풀필먼트 전용 상품",
            "오늘출발전용",
            "로켓그로스 전용 상품",
            "지그재그 전용",
            "[B2B]"
    };

    private boolean isExcludedFromSearch(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        for (String prefix : SEARCH_EXCLUDED_PREFIXES) {
            if (trimmed.startsWith(prefix)) return true;
        }
        return false;
    }

    /** 상품명/코드/바코드에 검색어가 포함된 상품을 캐시에서 찾습니다. 최대 30건. */
    public synchronized List<CatalogEntry> searchCatalog(String keyword) {
        refreshCatalogIfStale();
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        if (k.isEmpty()) return List.of();
        return catalogCache.stream()
                .filter(e -> !isExcludedFromSearch(e.name()))
                .filter(e -> e.productId().toLowerCase().contains(k)
                        || e.name().toLowerCase().contains(k)
                        || (e.barcode() != null && e.barcode().toLowerCase().contains(k)))
                .limit(30)
                .toList();
    }

    private void refreshCatalogIfStale() {
        if (System.currentTimeMillis() - catalogLoadedAt < CATALOG_TTL_MS && !catalogCache.isEmpty()) return;
        try {
            List<CatalogEntry> loaded = loadFullCatalog();
            // [개선] 받아온 상품이 있을 때만 캐시를 교체합니다.
            // 일시적으로 0건이 와도 기존에 잘 받아둔 목록을 날리지 않아, "떴다 사라졌다" 현상을 없앱니다.
            if (!loaded.isEmpty()) {
                catalogCache = loaded;
                catalogLoadedAt = System.currentTimeMillis();
                log.info("[EzAdmin] 상품 카탈로그 캐시 갱신 완료 — {}건", catalogCache.size());
            } else {
                log.warn("[EzAdmin] 이번 조회 0건 — 기존 캐시({}건) 유지, 다음 검색 때 다시 시도", catalogCache.size());
            }
        } catch (Exception e) {
            log.error("[EzAdmin] 상품 카탈로그 갱신 실패 — 기존 캐시({}건) 유지", catalogCache.size(), e);
        }
    }

    private List<CatalogEntry> loadFullCatalog() throws Exception {
        List<CatalogEntry> out = new ArrayList<>();
        if (partnerKey == null || partnerKey.isBlank() || domainKey == null || domainKey.isBlank()) {
            log.error("[EzAdmin] 인증키 미설정 — 카탈로그 조회를 건너뜁니다.");
            return out;
        }
        int page = 1;
        int limit = 500;
        for (int i = 0; i < 20; i++) { // 최대 20페이지 (=10,000건) 안전장치
            String query = "action=get_product_info"
                    + "&partner_key=" + enc(partnerKey)
                    + "&domain_key=" + enc(domainKey)
                    + "&date_type=reg_date"
                    + "&start_date=2015-01-01"
                    + "&end_date=" + java.time.LocalDate.now()
                    + "&page=" + page
                    + "&limit=" + limit;
            try {
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "?" + query))
                        .timeout(Duration.ofSeconds(30)) // [개선] 국외 서버가 느려도 끝까지 받도록 여유 있게
                        .GET().build();
                HttpResponse<String> res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                JsonNode root = om.readTree(res.body());
                if (root.path("error").asInt(-1) != 0) {
                    log.error("[EzAdmin] 상품조회 실패(page {}): {}", page, root.path("msg").asText(""));
                    break;
                }
                JsonNode data = root.path("data");
                if (!data.isArray() || data.isEmpty()) break;
                for (JsonNode item : data) {
                    String parentName = item.path("name").asText("");
                    JsonNode options = item.path("options");
                    if (options.isArray() && !options.isEmpty()) {
                        for (JsonNode opt : options) {
                            String pid = opt.path("product_id").asText("");
                            if (pid.isBlank()) continue;
                            String optText = opt.path("options").asText("");
                            String display = optText.isBlank() ? parentName : (parentName + " " + optText);
                            out.add(new CatalogEntry(pid, display, opt.path("barcode").asText(null)));
                        }
                    } else {
                        String pid = item.path("product_id").asText("");
                        if (!pid.isBlank()) out.add(new CatalogEntry(pid, parentName, null));
                    }
                }
                if (data.size() < limit) break; // 마지막 페이지
                page++;
                try { Thread.sleep(1000); } catch (InterruptedException ignored) {} // 권장 호출 간격
            } catch (Exception e) {
                // [개선] 한 페이지가 느리거나 실패해도, 여태 받은 상품은 버리지 않고 그대로 씁니다.
                // (예전엔 여기서 전부 버리고 처음부터 다시 하느라 "됐다 안 됐다" 했어요.)
                log.error("[EzAdmin] 카탈로그 {}페이지 조회 실패 — 여기까지 받은 {}건으로 진행합니다", page, out.size(), e);
                break;
            }
        }
        return out;
    }
}
