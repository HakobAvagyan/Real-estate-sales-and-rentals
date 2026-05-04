package org.example.dto.ratings;

import lombok.Value;

@Value
public class PropertyRatingSummaryDto {
    double averageStars;
    long reviewCount;

    public boolean hasReviews() {
        return reviewCount > 0;
    }
}
