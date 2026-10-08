package com.wingbling.logistics.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 매장 주소록 — 오프뷰티 등 거래처 매장의 정식 이름·주소·연락처.
 * 예전엔 대시보드 코드(index.html)에 이 목록을 그대로 박아뒀는데, 매장이 새로 생기거나
 * 주소가 바뀔 때마다 코드 배포가 필요했습니다. 이제 DB에 두고 화면에서 바로 추가/수정합니다.
 *
 * aliases: 거래처가 보내는 발주서에서 실제로 쓰는 다른 이름들(쉼표구분). 예를 들어 정식 이름은
 * "오프뷰티 모다천안아산(펜타포트)점"인데 발주서엔 "오프뷰티 천안아산점MO"라고 적혀 오는 경우,
 * 이 칸에 그 이름을 추가해두면 다음부터 자동으로 바로 찾습니다(코드 수정 없이).
 */
@Entity
@Table(name = "store_address")
@Getter
@Setter
@NoArgsConstructor
public class StoreAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "addr", columnDefinition = "text")
    private String addr;

    @Column(name = "phone", length = 60)
    private String phone;

    @Column(name = "aliases", columnDefinition = "text")
    private String aliases;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void touch() { this.updatedAt = LocalDateTime.now(); }
}
