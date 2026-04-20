package org.example.repository;

import org.example.model.PropertyUrgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PropertyUrgentRepository extends JpaRepository<PropertyUrgent, Integer> {

    @Query("SELECT pu.property.id FROM PropertyUrgent pu WHERE pu.isActive = true")
    List<Integer> findActivePropertyIds();
}