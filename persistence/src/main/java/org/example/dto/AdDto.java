package org.example.dto;

import lombok.Data;

@Data
public class AdDto {
    private int id;
    private String imagesUrl;
    private String videoUrl;
    private boolean isPayed;
    private int userId;
    private int adPlanId;
}
