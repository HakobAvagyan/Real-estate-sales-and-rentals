package org.example.mapper.location;

import org.example.dto.location.LocationDto;
import org.example.model.Location;
import org.example.model.LocationName;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    default LocationDto toLocationDto(Location location) {
        if (location == null) {
            return null;
        }
        LocationDto dto = new LocationDto();
        dto.setId(location.getId());
        if (location.getLocationName() != null) {
            dto.setRegion(location.getLocationName().getRegion());
            dto.setCity(location.getLocationName().getCity());
        }
        dto.setDistrict(location.getDistrict());
        dto.setStreet(location.getStreet());
        return dto;
    }

    default Location toLocation(LocationDto locationDto) {
        if (locationDto == null) {
            return null;
        }
        LocationName locationName = new LocationName();
        locationName.setRegion(locationDto.getRegion());
        locationName.setCity(locationDto.getCity());

        Location location = new Location();
        location.setId(locationDto.getId());
        location.setLocationName(locationName);
        location.setDistrict(locationDto.getDistrict());
        location.setStreet(locationDto.getStreet());
        return location;
    }
}
