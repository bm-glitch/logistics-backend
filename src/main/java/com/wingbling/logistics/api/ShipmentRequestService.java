package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.ShipmentRequest;
import com.wingbling.logistics.domain.ShipmentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentRequestService {

    private final ShipmentRequestRepository repo;

    /** SR-0001 형태로 자동 채번. (동시 접수량이 많아지면 DB 시퀀스로 교체 예정) */
    public synchronized String nextSrNo() {
        long n = repo.countAll() + 1;
        return "SR-%04d".formatted(n);
    }

    public ShipmentRequest create(CreateRequestDto dto) {
        ShipmentRequest r = ShipmentRequest.create(
                nextSrNo(), dto.requestTeam(), dto.requester(),
                dto.itemName(), dto.quantity(), dto.wantDate()
        );
        r.setOptionValue(dto.optionValue());
        r.setReceivePlace(dto.receivePlace());
        r.setNote(dto.note());
        r.setSku(dto.sku());
        if (dto.scope() != null) r.setScope(dto.scope());
        r.setAssignee(dto.assignee());
        r.setChannels(dto.channels());
        r.setProductsJson(dto.productsJson());
        r.setReceiverName(dto.receiverName());
        r.setReceiverPhone(dto.receiverPhone());
        r.setReceiverAddress(dto.receiverAddress());
        r.setReceiverMessage(dto.receiverMessage());
        r.setBillingType(dto.billingType());
        r.setSlackChannelId(dto.slackChannelId());
        r.setSlackUserId(dto.slackUserId());
        return repo.save(r);
    }

    /** 이미 진행중/완료인 건이 요청자에 의해 건드려졌는지 표시할지 판단하는 기준 */
    private static boolean needsAlert(String status) {
        return "진행중".equals(status) || "완료".equals(status);
    }

    /** 요청자가 취소한 경우. 삭제하지 않고 상태만 '취소'로 바꿔서 이력을 남깁니다. */
    public ShipmentRequest cancel(String srNo) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        String before = r.getStatus();
        if (needsAlert(before)) {
            r.setRequesterModifiedAt(LocalDateTime.now());
            r.setRequesterModifiedStatusBefore(before);
            r.setAlertAcknowledgedAt(null);
        }
        r.setStatus("취소");
        return r;
    }

    /**
     * 요청자가 내용을 수정한 경우. 상태(대기/진행중/완료)는 건드리지 않고,
     * 이미 진행중/완료였다면 물류팀이 확인해야 할 변경 알림을 남깁니다.
     */
    public ShipmentRequest edit(String srNo, CreateRequestDto dto) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));

        if (needsAlert(r.getStatus())) {
            r.setRequesterModifiedAt(LocalDateTime.now());
            r.setRequesterModifiedStatusBefore(r.getStatus());
            r.setAlertAcknowledgedAt(null);
        }

        r.setRequestTeam(dto.requestTeam());
        r.setRequester(dto.requester());
        r.setItemName(dto.itemName());
        r.setOptionValue(dto.optionValue());
        r.setQuantity(dto.quantity());
        r.setWantDate(dto.wantDate());
        r.setReceivePlace(dto.receivePlace());
        r.setNote(dto.note());
        r.setSku(dto.sku());
        r.setChannels(dto.channels());
        r.setProductsJson(dto.productsJson());
        r.setReceiverName(dto.receiverName());
        r.setReceiverPhone(dto.receiverPhone());
        r.setReceiverAddress(dto.receiverAddress());
        r.setReceiverMessage(dto.receiverMessage());
        r.setBillingType(dto.billingType());
        return r;
    }

    /** 물류팀이 알림을 확인(닫기)했을 때. */
    public ShipmentRequest acknowledgeAlert(String srNo) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setAlertAcknowledgedAt(LocalDateTime.now());
        return r;
    }

    /** 아직 확인 안 한, 이미 진행중/완료 상태에서 발생한 요청자 변경 건 목록 */
    public java.util.List<ShipmentRequest> findPendingAlerts() {
        return repo.findAll().stream()
                .filter(r -> r.getRequesterModifiedAt() != null && r.getAlertAcknowledgedAt() == null)
                .toList();
    }

    /** 물류팀이 수기로 택배사/송장번호를 등록. */
    public ShipmentRequest registerTracking(String srNo, String carrier, String trackingNo) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setCarrier(carrier);
        r.setTrackingNo(trackingNo);
        r.setTrackingRegisteredAt(LocalDateTime.now());
        return r;
    }

    /**
     * 이지어드민 관리번호(seq)를 연결합니다. 이후 백그라운드에서 자동으로
     * 송장 등록 여부를 확인해서, 등록되면 자동으로 채워 넣습니다.
     */
    public ShipmentRequest linkEzAdminOrder(String srNo, String seq) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setEzadminSeq(seq);
        return r;
    }

    /** 관리번호는 연결됐지만 아직 송장번호가 안 채워진 건들 — 백그라운드 작업이 확인할 대상 */
    public java.util.List<ShipmentRequest> findAwaitingTracking() {
        return repo.findAll().stream()
                .filter(r -> r.getEzadminSeq() != null && !r.getEzadminSeq().isBlank()
                        && (r.getTrackingNo() == null || r.getTrackingNo().isBlank()))
                .toList();
    }

    public ShipmentRequest updateStatus(String srNo, String status) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setStatus(status);
        if (!"보류".equals(status)) {
            r.setHoldReason(null); // 보류에서 벗어나면 사유도 함께 정리
        }
        if ("완료".equals(status)) {
            r.setCompletedAt(LocalDateTime.now());
        }
        return r; // JPA dirty checking, 트랜잭션 커밋 시 자동 반영
    }

    public ShipmentRequest hold(String srNo, String reason) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setStatus("보류");
        r.setHoldReason(reason);
        return r;
    }

    public ShipmentRequest markNotified(String srNo) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setNotifiedAt(LocalDateTime.now());
        return r;
    }
}