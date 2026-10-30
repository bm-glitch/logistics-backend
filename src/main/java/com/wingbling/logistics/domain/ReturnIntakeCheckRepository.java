package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReturnIntakeCheckRepository extends JpaRepository<ReturnIntakeCheck, Long> {
    Optional<ReturnIntakeCheck> findByEzadminSeq(String ezadminSeq);
}
