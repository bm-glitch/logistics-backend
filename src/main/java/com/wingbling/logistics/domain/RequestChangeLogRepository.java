package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestChangeLogRepository extends JpaRepository<RequestChangeLog, Long> {

    /** 특정 요청(sr)의 변경 이력 — 최신순 */
    List<RequestChangeLog> findBySrNoOrderByChangedAtDesc(String srNo);
}
