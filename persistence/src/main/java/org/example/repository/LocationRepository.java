package org.example.repository;

import org.example.model.Location;
import org.example.model.enums.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    Optional<Location> findByLocationNameRegion(Region region);

    Optional<Location> findByLocationNameCity(String city);

    Optional<Location> findByDistrict(String district);

    Optional<Location> findByStreet(String street);

    Optional<Location> findByLocationNameRegionAndLocationNameCityAndDistrictAndStreet(
            Region region, String city, String district, String street
    );

    Optional<Location> findByLocationNameIdAndDistrictAndStreet(int locationNameId, String district, String street);
}
