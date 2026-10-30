package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 재고 작업 현황 — 이지어드민에 기록된 모든 재고 변동(입고·출고·반품·조정 등)을
 * 작업자 제외 없이 전부 받아와, 날짜 → 작업자별로 모아 보여주기 위한 기록입니다.
 */
@Entity
@Table(name = "inventory_activity")
@Getter @Setter @NoArgsConstructor
public class InventoryActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ezadmin_seq", length = 40, nullable = false, unique = true)
    private String ezadminSeq;

    @Column(name = "work_date", length = 40)
    private String workDate;

    @Column(name = "job", length = 20)
    private String job;

    @Column(name = "product_id", length = 100)
    private String productId;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "product_name", length = 300)
    private String productName;

    @Column(name = "option_value", length = 200)
    private String optionValue;

    @Column(name = "qty")
    private int qty;

    @Column(name = "owner", length = 80)
    private String owner;

    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Column(name = "order_seq", length = 100)
    private String orderSeq;

    @Column(name = "sheet", length = 60)
    private String sheet;

    @Column(name = "sheet_name", length = 200)
    private String sheetName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
