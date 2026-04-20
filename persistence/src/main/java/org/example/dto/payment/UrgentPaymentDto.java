package org.example.dto.payment;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class UrgentPaymentDto {
    int propertyId;
    String propertyTitle;
    BigDecimal amount;
}