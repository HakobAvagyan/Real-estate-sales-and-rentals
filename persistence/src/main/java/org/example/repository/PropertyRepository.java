package org.example.repository;

import org.example.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Integer>, JpaSpecificationExecutor<Property> {
    List<Property> findAllByUserId(Integer userId);
}
