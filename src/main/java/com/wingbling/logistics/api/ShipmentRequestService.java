package com.wingbling.logistics.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wingbling.logistics.domain.ShipmentRequest;
import com.wingbling.logistics.domain.ShipmentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentRequestService {

    private final ShipmentRequestRepository repo;

    // 상품 목록(products_json) 파싱/직렬화에 사용
    private static final ObjectMapper OM = new ObjectMapper();

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

    /**
     * 송장 정보를 요청자에게 실제로 알림. 등록(registerTracking)과는 완전히 분리된 동작입니다.
     * 물류팀이 이 메서드를 명시적으로 호출해야만 Slack 알림이 나갑니다.
     */
    public ShipmentRequest markTrackingNotified(String srNo) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setTrackingNotifiedAt(LocalDateTime.now());
        return r;
    }

    /**
     * [요청유형] 요청의 유형(출고요청/재고확보/안전재고)을 지정합니다.
     * 안전재고 요청서 등에서 접수 직후 호출합니다.
     */
    public ShipmentRequest changeRequestType(String srNo, String type) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setRequestType(type == null || type.isBlank() ? "출고요청" : type);
        return r;
    }

    /**
     * [상품별 상태] 주문 안의 특정 상품(들)만 개별 상태로 표시합니다. (상품별 독립 보류/반려/교환)
     * lineStatus/lineReason 을 products_json 각 항목에 저장합니다.
     *   action  : "hold"(보류) | "reject"(반려) | "exchange"(교환요청) | "clear"(정상 복귀)
     *   indexes : products_json 배열에서 대상 상품의 위치(0부터)
     * 주문 전체 상태(대기/진행중/완료)는 건드리지 않고, 상품별로만 표시합니다.
     */
    public ShipmentRequest updateProductStatus(String srNo, String action, String reason, List<Integer> indexes) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));

        String lineStatus = switch (action == null ? "" : action) {
            case "hold" -> "보류";
            case "reject" -> "반려";
            case "exchange" -> "교환요청";
            default -> null; // clear = 상태 제거(정상)
        };

        try {
            String json = r.getProductsJson();
            List<Map<String, Object>> lines = (json == null || json.isBlank())
                    ? new ArrayList<>()
                    : OM.readValue(json, new TypeReference<List<Map<String, Object>>>() {});

            if (indexes != null) {
                for (Integer idx : indexes) {
                    if (idx == null || idx < 0 || idx >= lines.size()) continue;
                    Map<String, Object> line = lines.get(idx);
                    if (lineStatus == null) {
                        line.remove("lineStatus");
                        line.remove("lineReason");
                    } else {
                        line.put("lineStatus", lineStatus);
                        line.put("lineReason", reason == null ? "" : reason);
                    }
                }
            }
            r.setProductsJson(OM.writeValueAsString(lines));
        } catch (Exception e) {
            throw new RuntimeException("상품 상태 저장에 실패했습니다: " + e.getMessage(), e);
        }
        return r; // dirty checking으로 커밋 시 자동 저장
    }
}
