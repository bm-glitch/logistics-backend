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
        return repo.save(r);
    }

    public ShipmentRequest updateStatus(String srNo, String status) {
        ShipmentRequest r = repo.findBySrNo(srNo)
                .orElseThrow(() -> new EntityNotFoundException("요청 없음: " + srNo));
        r.setStatus(status);
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
