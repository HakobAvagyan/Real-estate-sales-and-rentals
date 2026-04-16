package org.example.repository;

import org.example.model.LocationName;
import org.example.model.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationNameRepository extends JpaRepository<LocationName, Integer> {
    Optional<LocationName> findByRegionAndCity(Region region, String city);
}
