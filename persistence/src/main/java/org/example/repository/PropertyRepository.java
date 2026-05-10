package org.example.repository;

import org.example.model.Property;
import org.example.model.enums.PropertyModerationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Integer>, JpaSpecificationExecutor<Property> {
    List<Property> findAllByUserId(Integer userId);

    List<Property> findAllByModerationStatusOrderByCreatedAtDesc(PropertyModerationStatus moderationStatus);

    long countByModerationStatus(PropertyModerationStatus moderationStatus);

    @Query("SELECT p.status, COUNT(p) FROM Property p GROUP BY p.status")
    List<Object[]> countGroupedByStatus();

    @Query("SELECT p.propertyType, COUNT(p) FROM Property p GROUP BY p.propertyType")
    List<Object[]> countGroupedByPropertyType();
}
