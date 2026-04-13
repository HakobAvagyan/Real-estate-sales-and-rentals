package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.location.LocationDto;
import org.example.dto.location.LocationNameDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.location.LocationMapper;
import org.example.model.Location;
import org.example.model.LocationName;
import org.example.model.enums.Region;
import org.example.repository.LocationNameRepository;
import org.example.repository.LocationRepository;
import org.example.service.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationNameRepository locationNameRepository;
    private final LocationMapper locationMapper;

    @Override
    public Optional<LocationDto> findByRegion(Region region) {
        return locationRepository.findByLocationNameRegion(region).map(locationMapper::toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByCity(String city) {
        return locationRepository.findByLocationNameCity(city).map(locationMapper::toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByDistrict(String district) {
        return locationRepository.findByDistrict(district).map(locationMapper::toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByStreet(String street) {
        return locationRepository.findByStreet(street).map(locationMapper::toLocationDto);
    }

    @Override
    public Optional<LocationDto> findByRegionAndCityAndDistrictAndStreet(Region region, String city, String district, String street) {
        return locationRepository
                .findByLocationNameRegionAndLocationNameCityAndDistrictAndStreet(region, city, district, street)
                .map(locationMapper::toLocationDto);
    }

    @Override
    public Optional<LocationDto> findById(int id) {
        return locationRepository.findById(id).map(locationMapper::toLocationDto);
    }

    @Override
    public LocationDto save(LocationDto locationDto) {
        Location location = new Location();
        location.setDistrict(locationDto.getDistrict());
        location.setStreet(locationDto.getStreet());
        location.setLocationName(getOrCreateLocationName(locationDto.getRegion(), locationDto.getCity()));
        Location savedLocation = locationRepository.save(location);
        return locationMapper.toLocationDto(savedLocation);
    }

    @Override
    public LocationDto update(LocationDto locationDto) {
        Location location = locationRepository.findById(locationDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND, locationDto.getId()));
        location.setDistrict(locationDto.getDistrict());
        location.setStreet(locationDto.getStreet());
        location.setLocationName(getOrCreateLocationName(locationDto.getRegion(), locationDto.getCity()));
        Location updatedLocation = locationRepository.save(location);
        return locationMapper.toLocationDto(updatedLocation);
    }

    @Override
    public void deleteByID(int id) {
        locationRepository.deleteById(id);
    }

    @Override
    public List<LocationDto> getAll() {
        return locationRepository.findAll().stream().map(locationMapper::toLocationDto).toList();
    }

    @Override
    public List<LocationNameDto> getAllLocationNames() {
        return locationNameRepository.findAll().stream().map(locationName -> {
            LocationNameDto dto = new LocationNameDto();
            dto.setId(locationName.getId());
            dto.setRegion(locationName.getRegion());
            dto.setCity(locationName.getCity());
            return dto;
        }).toList();
    }

    private LocationName getOrCreateLocationName(Region region, String city) {
        return locationNameRepository.findByRegionAndCity(region, city).orElseGet(() -> {
            LocationName locationName = new LocationName();
            locationName.setRegion(region);
            locationName.setCity(city);
            return locationNameRepository.save(locationName);
        });
    }
}
