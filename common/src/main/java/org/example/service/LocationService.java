package org.example.service;

import org.example.dto.LocationDto;

import java.util.List;

public interface LocationService {

//    Optional<Location> findById(Integer id);
//    Location save(Location location);
//    void deleteById(Integer id);
//    Location update(Location location);
//    Optional<Location> findByRegion(Region region);
//    Optional<Location> findByCity(String city);
//    Optional<Location> findByDistrict(String district);
//    Optional<Location> findByStreet(String street);
//    Optional<Location> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street);

    LocationDto create(LocationDto locationDto);
    LocationDto update(LocationDto locationDto);
    void deleteByID(int id);
    List<LocationDto> getAll();


}
