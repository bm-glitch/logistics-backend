package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchMemoryRepository extends JpaRepository<MatchMemory, Long> {

    /** 정규화 키로 학습된 매칭을 찾습니다. (있으면 갱신, 없으면 새로 저장) */
    Optional<MatchMemory> findByAliasKey(String aliasKey);
}
