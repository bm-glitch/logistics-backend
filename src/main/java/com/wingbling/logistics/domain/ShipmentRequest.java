package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipment_request")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShipmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sr_no", nullable = false, unique = true, length = 20)
    private String srNo;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt = LocalDateTime.now();

    @Column(name = "request_team", nullable = false, length = 50)
    private String requestTeam;

    @Column(name = "requester", nullable = false, length = 50)
    private String requester;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "want_date", nullable = false)
    private LocalDate wantDate;

    @Column(name = "receive_place", length = 100)
    private String receivePlace;

    @Column(name = "note", columnDefinition = "text")
    private String note;

    @Column(name = "sku", length = 50)
    private String sku;

    @Column(name = "option_value", length = 50)
    private String optionValue;

    /** team(물류 통합) | person(개인 요청) */
    @Column(name = "scope", nullable = false, length = 10)
    private String scope = "team";

    @Column(name = "assignee", length = 50)
    private String assignee;

    /** 대기 | 진행중 | 완료 | 보류 */
    @Column(name = "status", nullable = false, length = 10)
    private String status = "대기";

    @Column(name = "hold_reason", length = 200)
    private String holdReason;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "stock_phys")
    private Integer stockPhys;

    @Column(name = "stock_invoice")
    private Integer stockInvoice;

    @Column(name = "stock_pending")
    private Integer stockPending;

    @Column(name = "stock_checked_at")
    private LocalDateTime stockCheckedAt;

    @Column(name = "channels", columnDefinition = "text")
    private String channels;

    @Column(name = "products_json", columnDefinition = "text")
    private String productsJson;

    @Column(name = "receiver_name", length = 50)
    private String receiverName;

    @Column(name = "receiver_phone", length = 30)
    private String receiverPhone;

    @Column(name = "receiver_address", length = 200)
    private String receiverAddress;

    @Column(name = "receiver_message", length = 200)
    private String receiverMessage;

    /** paid(유상) | free(무상) */
    @Column(name = "billing_type", length = 10)
    private String billingType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static ShipmentRequest create(String srNo, String requestTeam, String requester,
                                         String itemName, Integer quantity, LocalDate wantDate) {
        ShipmentRequest r = new ShipmentRequest();
        r.srNo = srNo;
        r.requestTeam = requestTeam;
        r.requester = requester;
        r.itemName = itemName;
        r.quantity = quantity;
        r.wantDate = wantDate;
        return r;
    }

    @PreUpdate
    void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}