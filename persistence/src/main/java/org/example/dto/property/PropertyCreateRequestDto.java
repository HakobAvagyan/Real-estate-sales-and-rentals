package org.example.dto.property;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PropertyCreateRequestDto {
    private int userId;

    @Min(value = 1, message = "Location must be selected")
    private int locationId;

    private Integer locationNameId;
    private String district;
    private String street;

    @NotBlank(message = "Title must not be blank")
    @Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
    private String title;

    @Size(max = 3000, message = "Description must be at most 3000 characters")
    private String description;

    @Min(value = 1, message = "Surface must be at least 1 sq.m.")
    private int surface;

    private Integer roomsCount;
    private Integer bathroomsCount;
    private Integer floorCount;
    private int floor;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Status is required")
    private PropertyStatus status;

    @NotNull(message = "Property type is required")
    private PropertyType propertyType;

    private List<MultipartFile> images;
}
