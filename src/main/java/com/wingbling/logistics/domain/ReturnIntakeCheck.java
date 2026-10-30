package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "return_intake_check")
@Getter
@Setter
@NoArgsConstructor
public class ReturnIntakeCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ezadmin_seq", nullable = false, unique = true, length = 40)
    private String ezadminSeq;

    @Column(name = "sheet", length = 60)
    private String sheet;

    @Column(name = "sheet_name", length = 200)
    private String sheetName;

    @Column(name = "product_id", length = 100)
    private String productId;

    @Column(name = "barcode", length = 100)
    private String barcode;

    @Column(name = "product_name", length = 300)
    private String productName;

    @Column(name = "option_value", length = 200)
    private String optionValue;

    @Column(name = "qty", nullable = false)
    private int qty;

    @Column(name = "work_date", length = 40)
    private String workDate;

    @Column(name = "owner", length = 80)
    private String owner;

    @Column(name = "memo", columnDefinition = "text")
    private String memo;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @Column(name = "checked_by", length = 80)
    private String checkedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
