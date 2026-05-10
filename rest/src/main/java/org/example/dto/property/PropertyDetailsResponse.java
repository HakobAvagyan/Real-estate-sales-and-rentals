package org.example.dto.property;

import java.math.BigDecimal;
import java.util.List;

public record PropertyDetailsResponse(
        PropertyResponseDto property,
        List<PropertyResponseDto> similarProperties,
        BigDecimal convertedPrice,
        String currency
) {}
