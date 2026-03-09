package org.example.mapper;

import org.example.dto.LocationDto;
import org.example.model.Location;

public class LocationMapper {

    public static LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }
        LocationDto locationDto = new LocationDto();
        locationDto.setId(location.getId());
        locationDto.setRegion(location.getRegion());
        locationDto.setCity(location.getCity());
        locationDto.setDistrict(location.getDistrict());
        locationDto.setStreet(location.getStreet());
        return locationDto;
    }

    public static Location toLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        Location location = new Location();
        location.setId(locationDto.getId());
        location.setRegion(locationDto.getRegion());
        location.setCity(locationDto.getCity());
        location.setDistrict(locationDto.getDistrict());
        location.setStreet(locationDto.getStreet());
        return location;
    }

}
