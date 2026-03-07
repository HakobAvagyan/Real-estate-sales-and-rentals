package org.example.service;

import org.example.model.Location;
import org.example.model.enums.Region;

import java.util.Optional;

public interface LocationService {
    Optional<Location> findById(Integer id);

    void save(Location location);

    void deleteById(Integer id);

    Location update(Location location);

    Optional<Location> findByRegion(Region region);

    Optional<Location> findByCity(String city);

    Optional<Location> findByDistrict(String district);

    Optional<Location> findByStreet(String street);

    Optional<Location> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street);

}
