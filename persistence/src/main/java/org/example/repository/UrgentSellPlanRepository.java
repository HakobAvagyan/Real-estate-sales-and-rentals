package org.example.repository;

import org.example.model.UrgentSellPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrgentSellPlanRepository extends JpaRepository<UrgentSellPlan, Integer> {
    Optional<UrgentSellPlan> findFirstByIsActiveTrue();
}
