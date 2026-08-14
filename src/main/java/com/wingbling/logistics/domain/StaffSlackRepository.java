package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffSlackRepository extends JpaRepository<StaffSlack, Long> {

    /** 정규화된 이름 키로 직원 Slack 정보를 찾습니다. */
    Optional<StaffSlack> findByNameKey(String nameKey);
}
