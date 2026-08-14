package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 상품 매칭 학습(기억) — 타팀 발주서의 상품명/코드가 이지어드민 어떤 상품인지,
 * 물류 관리자가 수동으로 확정한 매핑을 저장해 둡니다.
 * 다음번 자동매칭 때 이 표를 최우선으로 참고해서, 같은 상품명/코드는 자동으로 정확히 매칭됩니다.
 *
 * alias_key : 타팀 상품명(또는 코드)을 정규화한 값. 화면(대시보드)에서 만들어 보냅니다.
 * product_id: 사람이 확정한 이지어드민 내부코드(S…).
 */
@Entity
@Table(name = "match_memory")
@Getter
@Setter
@NoArgsConstructor
public class MatchMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alias_key", nullable = false, unique = true, length = 300)
    private String aliasKey;

    @Column(name = "product_id", nullable = false, length = 50)
    private String productId;

    @Column(name = "option_value", length = 200)
    private String optionValue;

    /** 이지어드민 정식 상품명(참고용) */
    @Column(name = "product_name", length = 300)
    private String productName;

    /** 원본(타팀) 상품명(참고용) */
    @Column(name = "source_name", length = 300)
    private String sourceName;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
