package org.example.dto.ratings;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class PropertyRatingViewDto {
    int id;
    int stars;
    String reviewText;
    LocalDateTime ratedAt;
    int userId;
    String authorDisplayName;
}
