package com.wingbling.logistics.api;

import com.wingbling.logistics.domain.AsIntakeCheck;
import com.wingbling.logistics.domain.AsIntakeCheckRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A/S 입고 이중체크 — CS팀이 이지어드민에 등록한 입고(A/S로 받은 상품) 전표를 가져와서,
 * 물류팀이 실물을 바코드로 스캔해 한 번 더 확인(이중체크)하는 화면의 로직입니다.
 * (반품입고는 별도의 "반품 입고 확인" 화면에서 다룹니다 — 같은 전표가 두 화면에 겹치지 않도록
 *  작업유형으로 나눴습니다: 여기는 입고(in)만, 반품은 ReturnIntakeCheckService가 담당합니다.)
 *
 * 배경: CS팀이 A/S로 받은 상품을 이지어드민에 입고 등록만 하고 물류팀에 실물을 넘기는데,
 * 서로 대조할 서류가 없어서 가끔 전산엔 있는데 실물이 없는(분실) 사고가 뒤늦게 발견됩니다.
 * 그래서 CS팀 작업 내역을 자동으로 끌어와 체크리스트로 만들고, 물류팀이 받은 즉시
 * 바코드 스캔으로 맞춰보는 이중 확인 장치를 만듭니다.
 */
@Service
@RequiredArgsConstructor
public class AsIntakeCheckService {

    private static final Logger log = LoggerFactory.getLogger(AsIntakeCheckService.class);

    // 물류팀 자신의 작업은 이미 본인이 확인한 것이므로 이중체크 대상에서 뺍니다.
    private static final Set<String> EXCLUDED_OWNERS = Set.of("백화성", "유다영", "배영환");
    private static final Set<String> TARGET_JOBS = Set.of("in");

    private final AsIntakeCheckRepository repo;
    private final EzAdminService ezAdminService;

    /**
     * 이지어드민에서 지정한 기간(보름~한달 등, 화면에서 직접 고름)의 입고 이력을
     * 받아와 새로 나타난 것만 추가합니다. 이미 가지고 있는 건(ezadminSeq 기준)은
     * 체크 상태를 그대로 보존합니다.
     */
    @Transactional
    public int syncFromEzAdmin(LocalDate start, LocalDate end) {
        List<EzAdminService.StockTxEntry> entries = new ArrayList<>();
        entries.addAll(ezAdminService.getStockTxInfo(start, end, "in"));

        int added = 0;
        for (var e : entries) {
            if (e.seq() == null || e.seq().isBlank()) continue;
            if (!TARGET_JOBS.contains(e.job())) continue;
            if (EXCLUDED_OWNERS.contains(e.owner())) continue;
            if (repo.findByEzadminSeq(e.seq()).isPresent()) continue; // 이미 있음 — 체크 상태 보존

            AsIntakeCheck c = new AsIntakeCheck();
            c.setEzadminSeq(e.seq());
            c.setSheet(e.sheet());
            c.setSheetName(e.sheetName());
            c.setProductId(e.productId());
            c.setBarcode(e.barcode());
            c.setProductName(e.name());
            c.setOptionValue(e.options());
            c.setQty(e.qty());
            c.setWorkDate(e.crdate());
            c.setOwner(e.owner());
            c.setMemo(e.memo());
            repo.save(c);
            added++;
        }
        log.info("[A/S입고체크] 동기화 완료 — 새로 추가 {}건 (조회기간 {}~{})", added, start, end);
        return added;
    }

    public List<AsIntakeCheck> list() {
        return repo.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "workDate"));
    }

    /** 바코드 스캔으로 매칭된 항목들을 한 번에 "확인됨" 처리합니다. */
    @Transactional
    public List<AsIntakeCheck> markChecked(List<Long> ids, String checkedBy) {
        List<AsIntakeCheck> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Long id : ids) {
            repo.findById(id).ifPresent(c -> {
                c.setCheckedAt(now);
                c.setCheckedBy(checkedBy);
                result.add(repo.save(c));
            });
        }
        return result;
    }

    /** 잘못 체크했을 때 되돌리기. */
    @Transactional
    public AsIntakeCheck unmark(Long id) {
        AsIntakeCheck c = repo.findById(id).orElseThrow();
        c.setCheckedAt(null);
        c.setCheckedBy(null);
        return repo.save(c);
    }
}
