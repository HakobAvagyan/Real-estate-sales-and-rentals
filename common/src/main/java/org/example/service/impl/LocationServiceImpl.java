package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Location;
import org.example.model.enums.Region;
import org.example.repository.LocationRepository;
import org.example.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private  final LocationRepository locationRepository;

    @Override
    public Optional<Location> findById(Integer id) {
        return locationRepository.findById(id);
    }

    @Override
    public void save(Location location) {
        locationRepository.save(location);
    }

    @Override
    public void deleteById(Integer id) {
        locationRepository.deleteById(id);
    }

    @Override
    public Location update(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public Optional<Location> findByRegion(Region region) {
        return locationRepository.findByRegion(region);
    }

    @Override
    public Optional<Location> findByCity(String city) {
        return locationRepository.findByCity(city);
    }

    @Override
    public Optional<Location> findByDistrict(String district) {
        return locationRepository.findByDistrict(district);
    }

    @Override
    public Optional<Location> findByStreet(String street) {
        return locationRepository.findByStreet(street);
    }

    @Override
    public Optional<Location> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street) {
        return locationRepository.findByRegionAndCityAndDistrictAndStreet(region, city, district, street);
    }
}
