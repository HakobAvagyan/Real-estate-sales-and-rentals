package org.example.dto.ratings;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RatingsDto {
    private Integer id;
    private int rating;
    private String reviewText;
    private LocalDateTime ratedAt;
    private Integer propertyId;
    private Integer userId;
}
