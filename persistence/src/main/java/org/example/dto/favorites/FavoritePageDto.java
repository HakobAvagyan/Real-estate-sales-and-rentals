package org.example.dto.favorites;

import lombok.Data;
import org.example.dto.location.LocationDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.example.model.enums.Region;

import java.util.List;
import java.util.Map;

@Data
public class FavoritePageDto {

    private List<PropertyResponseDto> properties;
    private PropertyType[] propertyTypes;
    private PropertyStatus[] propertyStatuses;
    private Region[] regions;
    private Map<Integer, LocationDto> locationMap;
    private List<Integer> urgentPropertyIds;

}
