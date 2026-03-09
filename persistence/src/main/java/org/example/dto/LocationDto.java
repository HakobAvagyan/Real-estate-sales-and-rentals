package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.model.enums.Region;

@Getter
@Setter
public class LocationDto {

    private int id;
    private Region region;
    private String city;
    private String district;
    private String street;

}
