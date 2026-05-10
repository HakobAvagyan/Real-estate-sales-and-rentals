package org.example.repository;

import org.example.model.Property360;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Property360Repository extends JpaRepository<Property360, Integer> {
    Optional<Property360> findByPropertyId(int propertyId);
    void deleteByPropertyId(int propertyId);
}