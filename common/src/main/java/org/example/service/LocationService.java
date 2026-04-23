package org.example.service;

import org.example.dto.location.LocationDto;
import org.example.dto.location.LocationNameDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LocationService {

    Map<Integer,LocationDto> getLocationMap();

    Optional<LocationDto> findById(int id);

    LocationDto save(LocationDto locationDto);

    LocationDto update(LocationDto locationDto);

    List<LocationDto> getAll();

    List<LocationNameDto> getAllLocationNames();


}
