package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.ShipmentRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 이지어드민 관리번호(seq)가 연결된 요청들을 주기적으로 확인해서,
 * 송장번호가 등록되면 자동으로 채워 넣고, 요청자에게 Slack 송장 알림까지 자동으로 보내는 백그라운드 작업.
 *
 * (참고) 이 스케줄러가 돌려면 LogisticsApplication.java에 @EnableScheduling 이 있어야 하는데,
 * 이미 추가돼 있습니다. 따로 손댈 것 없어요.
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
                    // 1) 송장번호 자동 저장
                    shipmentRequestService.registerTracking(
                            r.getSrNo(), info.transCorp(), info.transNo());
                    log.info("[송장자동확인] {} 송장 자동 등록 — {} {}", r.getSrNo(), info.transCorp(), info.transNo());

                    // 2) 요청자에게 송장 알림 자동 발송 (Slack에 연결된 요청만).
                    //    findAwaitingTracking()은 송장번호가 빈 건만 돌려주므로, 이 알림은 건당 딱 한 번만 나갑니다.
                    ShipmentRequest updated = shipmentRequestService.markTrackingNotified(r.getSrNo());
                    if (updated.getSlackChannelId() != null && !updated.getSlackChannelId().isBlank()) {
                        String trackingsText = shipmentRequestService.trackingsText(updated);
                        String label = slackService.friendlyLabel(
                                updated.getReceivedAt() == null ? null : updated.getReceivedAt().toLocalDate(),
                                updated.getRequester(), updated.getReceiverName());
                        slackService.async(() -> slackService.notifyTracking(
                                updated.getSlackChannelId(), updated.getSlackUserId(),
                                updated.getSrNo(), trackingsText, label));
                        log.info("[송장자동확인] {} 요청자 송장 알림 자동 발송", updated.getSrNo());
                    } else {
                        log.info("[송장자동확인] {} 송장 등록했으나 Slack 연결이 없어 요청자 알림은 생략", updated.getSrNo());
                    }
                }
                Thread.sleep(1000); // 이지어드민 권장 호출 간격
            } catch (Exception e) {
                log.error("[송장자동확인] {} 확인 중 오류", r.getSrNo(), e);
            }
        }
    }
}
