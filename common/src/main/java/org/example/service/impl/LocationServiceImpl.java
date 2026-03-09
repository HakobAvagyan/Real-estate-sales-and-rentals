package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.LocationDto;
import org.example.mapper.LocationMapper;
import org.example.model.Location;
import org.example.model.enums.Region;
import org.example.repository.LocationRepository;
import org.example.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private  final LocationRepository locationRepository;

    @Override
    public Optional<LocationDto> findByRegion(Region region) {
        return locationRepository.findByRegion(region).map(LocationMapper :: toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByCity(String city) {
        return locationRepository.findByCity(city).map(LocationMapper :: toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByDistrict(String district) {
        return locationRepository.findByDistrict(district).map(LocationMapper :: toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByStreet(String street) {
        return locationRepository.findByStreet(street).map(LocationMapper :: toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street) {
        return locationRepository.findByRegionAndCityAndDistrictAndStreet(region, city, district, street).map(LocationMapper :: toLocationDto);
    }

    @Override
    public Optional<LocationDto> findById(int id) {
        return locationRepository.findById(id).map(LocationMapper :: toLocationDto);
    }

    @Override
    public LocationDto save(LocationDto locationDto) {
        Location location = LocationMapper.toLocation(locationDto);
        Location savedLocation = locationRepository.save(location);
        return LocationMapper.toLocationDto(savedLocation);
    }

    @Override
    public LocationDto update(LocationDto locationDto) {
        Location location = LocationMapper.toLocation(locationDto);
        Location updatedLocation = locationRepository.save(location);
        return LocationMapper.toLocationDto(updatedLocation);
    }

    @Override
    public void deleteByID(int id) {
        locationRepository.deleteById(id);
    }

    @Override
    public List<LocationDto> getAll() {
        return locationRepository.findAll().stream().map(LocationMapper :: toLocationDto).toList();
    }


}
