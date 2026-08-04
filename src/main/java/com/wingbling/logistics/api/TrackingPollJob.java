package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.ShipmentRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 이지어드민 관리번호(seq)가 연결된 요청들을 주기적으로 확인해서,
 * 송장번호가 등록되면 자동으로 채워 넣고 Slack으로 알려주는 백그라운드 작업.
 *
 * 이 기능이 실제로 동작하려면, LogisticsApplication.java(스프링 부트 메인 클래스)에
 * EnableScheduling 어노테이션을 하나 추가해야 합니다. (이 파일만으로는 부족함)
 */
@Component
@RequiredArgsConstructor
public class TrackingPollJob {

    private static final Logger log = LoggerFactory.getLogger(TrackingPollJob.class);

    private final ShipmentRequestService shipmentRequestService;
    private final EzAdminService ezAdminService;
    private final SlackService slackService;

    /** 5분마다 실행. 이지어드민 호출 가이드(1초 간격 권장)를 지키기 위해 건별로 살짝 텀을 둡니다. */
    @Scheduled(fixedDelay = 5 * 60 * 1000)
    public void checkLinkedOrders() {
        var waiting = shipmentRequestService.findAwaitingTracking();
        if (waiting.isEmpty()) return;

        log.info("[송장자동확인] 확인 대상 {}건", waiting.size());
        for (ShipmentRequest r : waiting) {
            try {
                var info = ezAdminService.checkOrderTracking(r.getEzadminSeq());
                if (info != null && info.transNo() != null) {
                    var updated = shipmentRequestService.registerTracking(
                            r.getSrNo(), info.transCorp(), info.transNo());
                    log.info("[송장자동확인] {} 송장 자동 등록 — {} {}", r.getSrNo(), info.transCorp(), info.transNo());

                    if (updated.getSlackChannelId() != null && !updated.getSlackChannelId().isBlank()) {
                        slackService.notifyTracking(
                                updated.getSlackChannelId(), updated.getSlackUserId(),
                                updated.getSrNo(), updated.getCarrier(), updated.getTrackingNo());
                    }
                }
                Thread.sleep(1000); // 이지어드민 권장 호출 간격
            } catch (Exception e) {
                log.error("[송장자동확인] {} 확인 중 오류", r.getSrNo(), e);
            }
        }
    }
}