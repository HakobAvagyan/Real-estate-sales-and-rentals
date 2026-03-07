package org.example.repository;

import org.example.model.Location;
import org.example.model.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findByRegion(Region region);

    Optional<Location> findByCity(String city);

    Optional<Location> findByDistrict(String district);

    Optional<Location> findByStreet(String street);

    Optional<Location> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street);
}
