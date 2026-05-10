package org.example.dto.property;

import org.example.dto.ratings.PropertyRatingSummaryDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record PropertyListResponse(
        List<PropertyResponseDto> properties,
        Map<Integer, PropertyRatingSummaryDto> ratingSummaries,
        Map<Integer, Long> commentCounts,
        Map<Integer, BigDecimal> convertedPrices,
        String currency
) {}
