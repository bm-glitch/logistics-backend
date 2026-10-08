package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.StockWatch;
import com.wingbling.logistics.domain.StockWatchRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 안전재고 임계치 감시 — 등록해둔 상품의 이지어드민 실시간 재고를 기준 수량과 비교해서,
 * 기준보다 적으면(주문이 들어와 재고부족이 터지기 전에) 미리 물류팀에 Slack으로 알립니다.
 * 같은 상품이 계속 기준 미달이어도 하루에 한 번만 알리도록 lastAlertDate로 걸러냅니다.
 * (실제 체크 시각은 StockAlertJob — 매일 09:00/14:00)
 */
@Service
@RequiredArgsConstructor
public class StockWatchService {

    private static final Logger log = LoggerFactory.getLogger(StockWatchService.class);

    private final StockWatchRepository repo;
    private final EzAdminService ezAdminService;
    private final SlackService slackService;

    @Transactional
    public void runCheck() {
        List<StockWatch> watches = repo.findAll();
        if (watches.isEmpty()) return;

        List<String> codes = watches.stream()
                .map(StockWatch::getProductCode)
                .filter(c -> c != null && !c.isBlank())
                .distinct().toList();

        Map<String, EzAdminService.StockLine> stock;
        try {
            stock = ezAdminService.lookup(codes);
        } catch (Exception e) {
            log.error("[안전재고] 이지어드민 조회 실패", e);
            return;
        }

        LocalDate today = LocalDate.now();
        List<StockWatch> newlyLow = new ArrayList<>();

        for (StockWatch w : watches) {
            var line = stock.get(w.getProductCode());
            Integer available = (line == null) ? null : line.available();
            w.setLastCheckedQty(available);
            w.setLastCheckedAt(LocalDateTime.now());

            if (available != null && available < w.getThresholdQty() && !today.equals(w.getLastAlertDate())) {
                w.setLastAlertDate(today);
                newlyLow.add(w);
            }
        }
        repo.saveAll(watches);

        if (!newlyLow.isEmpty()) {
            log.info("[안전재고] 임계치 미달 {}건 — 물류팀 알림 발송", newlyLow.size());
            slackService.async(() -> slackService.notifyStockShortageWatch(newlyLow));
        }
    }
}
