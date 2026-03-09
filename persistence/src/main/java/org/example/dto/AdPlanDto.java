package org.example.dto;

import lombok.Data;

@Data
public class AdPlanDto {
    private int id;
    private String name;
    private double price;
    private int durationDays;
}
