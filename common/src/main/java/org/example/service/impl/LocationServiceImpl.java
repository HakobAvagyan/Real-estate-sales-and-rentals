package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.LocationDto;
import org.example.mapper.LocationMapper;
import org.example.model.Location;
import org.example.repository.LocationRepository;
import org.example.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private  final LocationRepository locationRepository;

    @Override
    public LocationDto create(LocationDto locationDto) {
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


//
//    @Override
//    public Optional<Location> findById(Integer id) {
//        return locationRepository.findById(id);
//    }
//
//    @Override
//    public Location save(Location location) {
//        return locationRepository.save(location);
//    }
//
//    @Override
//    public void deleteById(Integer id) {
//        locationRepository.deleteById(id);
//    }
//
//    @Override
//    public Location update(Location location) {
//        return locationRepository.save(location);
//    }
//
//    @Override
//    public Optional<Location> findByRegion(Region region) {
//        return locationRepository.findByRegion(region);
//    }
//
//    @Override
//    public Optional<Location> findByCity(String city) {
//        return locationRepository.findByCity(city);
//    }
//
//    @Override
//    public Optional<Location> findByDistrict(String district) {
//        return locationRepository.findByDistrict(district);
//    }
//
//    @Override
//    public Optional<Location> findByStreet(String street) {
//        return locationRepository.findByStreet(street);
//    }
//
//    @Override
//    public Optional<Location> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street) {
//        return locationRepository.findByRegionAndCityAndDistrictAndStreet(region, city, district, street);
//    }
}
