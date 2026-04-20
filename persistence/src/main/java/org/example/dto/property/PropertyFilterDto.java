package org.example.dto.property;

import lombok.Data;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.example.model.enums.Region;

import java.math.BigDecimal;

@Data
public class PropertyFilterDto {
    private PropertyType propertyType;
    private PropertyStatus status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minSurface;
    private Integer maxSurface;
    private Integer minRooms;
    private Integer maxRooms;
    private Integer minBathrooms;
    private Integer minFloor;
    private Integer maxFloor;
    private Region region;
    private String city;

    public boolean isEmpty() {
        return propertyType == null && status == null
                && minPrice == null && maxPrice == null
                && minSurface == null && maxSurface == null
                && minRooms == null && maxRooms == null
                && minBathrooms == null
                && minFloor == null && maxFloor == null
                && region == null && (city == null || city.isBlank());
    }
}
