package org.example.dto.property;

import lombok.Data;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;

import java.math.BigDecimal;

@Data
public class PropertyCreateRequestDto {
    private int userId;
    private int locationId;
    private Integer locationNameId;
    private String district;
    private String street;
    private String title;
    private String description;
    private int surface;
    private Integer roomsCount;
    private Integer bathroomsCount;
    private Integer floorCount;
    private int floor;
    private BigDecimal price;
    private PropertyStatus status;
    private PropertyType propertyType;
}
