package org.example.dto.location;

import lombok.Data;
import org.example.model.enums.Region;

@Data
public class LocationDto {

    private int id;
    private Region region;
    private String city;
    private String district;
    private String street;

}
