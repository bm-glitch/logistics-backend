package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.InventoryActivity;
import com.wingbling.logistics.domain.InventoryActivityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 재고 작업 현황 — 이지어드민에 기록된 재고 변동(입고·출고·반품·조정 등)을 작업자
 * 구분 없이 전부 받아와, 물류팀이 날짜별·작업자별로 누가 무엇을 건드렸는지 확인하는 화면의 로직입니다.
 * (A/S 입고 이중체크와 달리 특정 작업자를 빼지 않고, 특정 작업 유형으로 거르지도 않습니다.)
 */
@Service
@RequiredArgsConstructor
public class InventoryActivityService {

    private static final Logger log = LoggerFactory.getLogger(InventoryActivityService.class);

    private final InventoryActivityRepository repo;
    private final EzAdminService ezAdminService;

    /** 지정한 기간의 전체 재고 변동 이력을 받아와 새로 나타난 것만 추가합니다. */
    @Transactional
    public int syncFromEzAdmin(LocalDate start, LocalDate end) {
        List<EzAdminService.StockTxEntry> entries = ezAdminService.getStockTxInfo(start, end, null);

        int added = 0;
        for (var e : entries) {
            if (e.seq() == null || e.seq().isBlank()) continue;
            if (repo.existsByEzadminSeq(e.seq())) continue; // 이미 있음

            InventoryActivity a = new InventoryActivity();
            a.setEzadminSeq(e.seq());
            a.setWorkDate(e.crdate());
            a.setJob(e.job());
            a.setProductId(e.productId());
            a.setBarcode(e.barcode());
            a.setProductName(e.name());
            a.setOptionValue(e.options());
            a.setQty(e.qty());
            a.setOwner(e.owner());
            a.setMemo(e.memo());
            a.setOrderSeq(e.orderSeq());
            a.setSheet(e.sheet());
            a.setSheetName(e.sheetName());
            repo.save(a);
            added++;
        }
        log.info("[재고작업현황] 동기화 완료 — 새로 추가 {}건 (조회기간 {}~{})", added, start, end);
        return added;
    }

    public List<InventoryActivity> list() {
        return repo.findAll(Sort.by(Sort.Direction.DESC, "workDate"));
    }
}
