package com.wingbling.logistics.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wingbling.logistics.domain.ShipmentRequest;
import com.wingbling.logistics.domain.ShipmentRequestRepository;
import com.wingbling.logistics.domain.RequestChangeLog;
import com.wingbling.logistics.domain.RequestChangeLogRepository;
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
    private final RequestChangeLogRepository changeLogRepo;
    private final StaffDirectoryService staffDirectory;   // 요청자 Slack 주소록

    // 상품 목록(products_json) 파싱/직렬화에 사용
    private static final ObjectMapper OM = new ObjectMapper();

    /** 요청의 주요 필드를 JSON 한 덩어리로 스냅샷 — 변경 전/후 비교 기록용 */
    private String snapshot(ShipmentRequest r) {
        ObjectNode m = OM.createObjectNode();
        m.put("team", r.getRequestTeam());
        m.put("requester", r.getRequester());
        m.put("assignee", r.getAssignee());
        m.put("itemName", r.getItemName());
        m.put("optionValue", r.getOptionValue());
        m.put("quantity", r.getQuantity());
        m.put("wantDate", r.getWantDate() == null ? null : r.getWantDate().toString());
        m.put("receivePlace", r.getReceivePlace());
        m.put("note", r.getNote());
        m.put("channels", r.getChannels());
        m.put("productsJson", r.getProductsJson());
        m.put("receiverName", r.getReceiverName());
        m.put("receiverPhone", r.getReceiverPhone());
        m.put("receiverAddress", r.getReceiverAddress());
        m.put("receiverMessage", r.getReceiverMessage());
        m.put("billingType", r.getBillingType());
        m.put("status", r.getStatus());
        try { return OM.writeValueAsString(m); } catch (Exception e) { return "{}"; }
    }

    /** 변경 이력 한 줄 저장 */
    private void logChange(String srNo, String action, String actor, String reason,
                           String beforeJson, String afterJson) {
        changeLogRepo.save(RequestChangeLog.of(srNo, action, actor, reason, beforeJson, afterJson));
    }

    /** 특정 요청의 변경 이력(최신순) */
    public List<RequestChangeLog> findHistory(String srNo) {
        return changeLogRepo.findBySrNoOrderByChangedAtDesc(srNo);
    }

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

        // 요청자 Slack 주소록 연동 (모든 요청 생성이 이 곳을 거칩니다):
        //  · Slack으로 온 요청(slackUserId 있음) → 이름↔SlackID를 자동 저장(학습)
        //  · 대장부/모바일 요청(slackUserId 없음) → 주소록에서 SlackID를 찾아 붙여 알림이 가게 함
        try {
            if (dto.slackUserId() != null && !dto.slackUserId().isBlank()) {
                staffDirectory.remember(dto.requester(), dto.slackUserId(), dto.slackChannelId());
            } else {
                var s = staffDirectory.lookup(dto.requester());
                if (s != null && s.getSlackUserId() != null && !s.getSlackUserId().isBlank()) {
                    r.setSlackUserId(s.getSlackUserId());
                    if (r.getSlackChannelId() == null || r.getSlackChannelId().isBlank()) {
                        r.setSlackChannelId(s.getSlackChannelId());
                    }
                }
            }
        } catch (Exception ignore) { /* 주소록 연동 실패해도 요청 저장은 정상 진행 */ }

        return repo.save(r);
    }

    /** 이미 진행중/완료인 건이 요청자에 의해 건드려졌는지 표시할지 판단하는 기준 */
    private static boolean needsAlert(String status) {
        return "진행중".equals(status) || "완료".equals(status);
    }

    /** 요청자가 취소한 경우. 삭제하지 않고 상태만 '취소'로 바꿔서 이력을 남깁니다.
     *  누가·왜 취소했는지(actor/reason)를 변경 이력에 함께 기록합니다. */
    public ShipmentRequest cancel(String srNo, String actor, String reason) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        String beforeSnap = snapshot(r);
        String prevStatus = r.getStatus();
        if (needsAlert(prevStatus)) {
            r.setRequesterModifiedAt(LocalDateTime.now());
            r.setRequesterModifiedStatusBefore(prevStatus);
            r.setAlertAcknowledgedAt(null);
        }
        r.setStatus("취소");
        logChange(srNo, "취소", actor, reason, beforeSnap, snapshot(r));
        return r;
    }

    /**
     * 요청자가 내용을 수정한 경우. 상태(대기/진행중/완료)는 건드리지 않고,
     * 이미 진행중/완료였다면 물류팀이 확인해야 할 변경 알림을 남깁니다.
     */
    public ShipmentRequest edit(String srNo, CreateRequestDto dto, String actor, String reason) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));

        String beforeSnap = snapshot(r);

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

        logChange(srNo, "수정", actor, reason, beforeSnap, snapshot(r));
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

    /** 물류팀이 수기로 택배사/송장번호를 등록. (여러 개 지원 — 같은 송장번호는 중복 추가하지 않고 뒤에 덧붙임) */
    public ShipmentRequest registerTracking(String srNo, String carrier, String trackingNo) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        if (trackingNo != null && !trackingNo.isBlank()) {
            List<Map<String, String>> list = readTrackings(r);
            boolean exists = list.stream().anyMatch(t -> trackingNo.equals(t.get("trackingNo")));
            if (!exists) {
                var m = new java.util.LinkedHashMap<String, String>();
                m.put("carrier", carrier == null ? "" : carrier);
                m.put("trackingNo", trackingNo.trim());
                list.add(m);
            }
            syncTrackings(r, list);
        }
        return r;
    }

    /** 여러 송장을 통째로 교체 저장. (물류대장 송장 시트에서 +추가/삭제 후 저장할 때 사용) */
    public ShipmentRequest setTrackings(String srNo, List<Map<String, String>> trackings) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        List<Map<String, String>> clean = new ArrayList<>();
        if (trackings != null) {
            for (Map<String, String> t : trackings) {
                String no = t == null ? null : t.get("trackingNo");
                if (no == null || no.isBlank()) continue;
                var m = new java.util.LinkedHashMap<String, String>();
                m.put("carrier", t.getOrDefault("carrier", "") == null ? "" : t.getOrDefault("carrier", ""));
                m.put("trackingNo", no.trim());
                clean.add(m);
            }
        }
        syncTrackings(r, clean);
        return r;
    }

    /** 송장 목록을 JSON에 저장하고, 하위호환용 단일 필드(carrier/trackingNo)와 등록시각을 맞춰줍니다. */
    private void syncTrackings(ShipmentRequest r, List<Map<String, String>> list) {
        try { r.setTrackingsJson(OM.writeValueAsString(list)); } catch (Exception ignored) {}
        if (list.isEmpty()) {
            r.setCarrier(null);
            r.setTrackingNo(null);
        } else {
            r.setCarrier(list.get(0).get("carrier"));
            r.setTrackingNo(list.get(0).get("trackingNo"));
            r.setTrackingRegisteredAt(LocalDateTime.now());
        }
    }

    /** 저장된 송장 목록을 읽습니다. 목록이 비었는데 예전 단일 송장이 있으면 그걸 1개짜리 목록으로 돌려줍니다. */
    public List<Map<String, String>> readTrackings(ShipmentRequest r) {
        List<Map<String, String>> list = new ArrayList<>();
        String j = r.getTrackingsJson();
        if (j != null && !j.isBlank()) {
            try { list = OM.readValue(j, new TypeReference<List<Map<String, String>>>() {}); }
            catch (Exception e) { list = new ArrayList<>(); }
        }
        if (list.isEmpty() && r.getTrackingNo() != null && !r.getTrackingNo().isBlank()) {
            var m = new java.util.LinkedHashMap<String, String>();
            m.put("carrier", r.getCarrier() == null ? "" : r.getCarrier());
            m.put("trackingNo", r.getTrackingNo());
            list.add(m);
        }
        return list;
    }

    /** 요청자 알림용 — 여러 송장을 사람이 읽기 좋게 줄바꿈으로 이어붙입니다. 예: "CJ대한통운 · 123\n한진택배 · 456" */
    public String trackingsText(ShipmentRequest r) {
        List<Map<String, String>> list = readTrackings(r);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append("\n");
            String c = list.get(i).get("carrier");
            String no = list.get(i).get("trackingNo");
            sb.append((c == null || c.isBlank()) ? "-" : c).append(" · ").append(no == null ? "" : no);
        }
        return sb.toString();
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
