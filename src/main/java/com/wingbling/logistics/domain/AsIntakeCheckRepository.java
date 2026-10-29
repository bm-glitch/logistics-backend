package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AsIntakeCheckRepository extends JpaRepository<AsIntakeCheck, Long> {
    Optional<AsIntakeCheck> findByEzadminSeq(String ezadminSeq);
}
