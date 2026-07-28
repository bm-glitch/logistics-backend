package com.wingbling.logistics.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShipmentRequestRepository extends JpaRepository<ShipmentRequest, Long> {

    Optional<ShipmentRequest> findBySrNo(String srNo);

    List<ShipmentRequest> findByScopeOrderByWantDateAsc(String scope);

    @Query("SELECT COUNT(r) FROM ShipmentRequest r")
    long countAll();
}
