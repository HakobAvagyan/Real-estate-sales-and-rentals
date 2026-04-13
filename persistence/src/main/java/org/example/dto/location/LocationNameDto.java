package org.example.dto.location;

import lombok.Data;
import org.example.model.enums.Region;

@Data
public class LocationNameDto {
    private int id;
    private Region region;
    private String city;
}
