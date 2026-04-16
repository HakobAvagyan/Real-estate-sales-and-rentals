package org.example.dto.property;

import lombok.Builder;
import lombok.Value;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Value
@Builder
public class PropertyResponseDto {
    int id;
    int userId;
    int locationId;
    String title;
    String description;
    int surface;
    Integer roomsCount;
    Integer bathroomsCount;
    Integer floorCount;
    int floor;
    BigDecimal price;
    LocalDate createdAt;
    PropertyStatus status;
    PropertyType propertyType;
    List<String> imageUrls;
}
