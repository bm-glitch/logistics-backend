package com.wingbling.logistics.api;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 안전재고 임계치 체크 — 매일 09:00 / 14:00(한국시간)에 등록된 상품들의 이지어드민 재고를
 * 기준 수량과 비교해서, 기준 미달이면 물류팀에 Slack 알림을 보냅니다.
 * (참고) LogisticsApplication.java에 @EnableScheduling 이 이미 있어서 따로 손댈 것 없어요.
 */
@Component
@RequiredArgsConstructor
public class StockAlertJob {

    private static final Logger log = LoggerFactory.getLogger(StockAlertJob.class);

    private final StockWatchService stockWatchService;

    @Scheduled(cron = "0 0 9,14 * * *", zone = "Asia/Seoul")
    public void checkThresholds() {
        try {
            stockWatchService.runCheck();
        } catch (Exception e) {
            log.error("[안전재고] 임계치 체크 중 오류", e);
        }
    }
}
