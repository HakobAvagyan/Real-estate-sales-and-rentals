package org.example.service;

import org.example.dto.payment.UrgentPaymentDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Optional<PropertyResponseDto> getRentableProperty(int propertyId);

    UrgentPaymentDto getUrgentPaymentDetails(int propertyId);

    void confirmBooking(int propertyId, BigDecimal amount, LocalDate startDate, LocalDate endDate, int guests, User user);

    void confirmUrgent(int propertyId, User user);

    List<Integer> getActiveUrgentPropertyIds();
}