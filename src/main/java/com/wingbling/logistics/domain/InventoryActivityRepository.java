package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryActivityRepository extends JpaRepository<InventoryActivity, Long> {
    boolean existsByEzadminSeq(String ezadminSeq);
}
