package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_watch")
@Getter
@Setter
@NoArgsConstructor
public class StockWatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_code", nullable = false, length = 100)
    private String productCode;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "threshold_qty", nullable = false)
    private int thresholdQty;

    @Column(name = "last_checked_qty")
    private Integer lastCheckedQty;

    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;

    @Column(name = "last_alert_date")
    private LocalDate lastAlertDate;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void touch() { this.updatedAt = LocalDateTime.now(); }
}
