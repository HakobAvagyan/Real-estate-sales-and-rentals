package org.example.service;

import org.example.dto.LocationDto;
import org.example.model.enums.Region;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    Optional<LocationDto> findByRegion(Region region);

    Optional<LocationDto> findByCity(String city);

    Optional<LocationDto> findByDistrict(String district);

    Optional<LocationDto> findByStreet(String street);

    Optional<LocationDto> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street);

    Optional<LocationDto> findById(int id);

    LocationDto save(LocationDto locationDto);

    LocationDto update(LocationDto locationDto);

    void deleteByID(int id);

    List<LocationDto> getAll();


}
