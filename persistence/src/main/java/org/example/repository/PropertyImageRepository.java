package org.example.repository;

import org.example.model.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Integer> {
    List<PropertyImage> findAllByPropertyId(int propertyId);
}
