package org.example.service;

import org.example.dto.ratings.PropertyRatingSummaryDto;
import org.example.dto.ratings.PropertyRatingViewDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RatingsService {

    PropertyRatingSummaryDto getSummary(int propertyId);

    Map<Integer, PropertyRatingSummaryDto> getSummariesForPropertyIds(Collection<Integer> propertyIds);

    List<PropertyRatingViewDto> listReviewsForProperty(int propertyId);

    Optional<PropertyRatingViewDto> findReviewByUser(int propertyId, int userId);

    void upsertReview(int userId, int propertyId, int stars, String reviewText);
}
