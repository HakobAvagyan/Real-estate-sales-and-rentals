package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.payment.UrgentPaymentDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.Booking;
import org.example.model.Payment;
import org.example.model.PropertyUrgent;
import org.example.model.UrgentSellPlan;
import org.example.model.User;
import org.example.model.enums.PaymentStatus;
import org.example.model.enums.PropertyStatus;
import org.example.repository.BookingRepository;
import org.example.repository.PaymentRepository;
import org.example.repository.PropertyRepository;
import org.example.repository.PropertyUrgentRepository;
import org.example.repository.UrgentSellPlanRepository;
import org.example.service.PaymentService;
import org.example.service.PropertyService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal URGENT_PRICE = new BigDecimal("19");

    private final PropertyService propertyService;
    private final PropertyRepository propertyRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PropertyUrgentRepository propertyUrgentRepository;
    private final UrgentSellPlanRepository urgentSellPlanRepository;

    @Override
    public Optional<PropertyResponseDto> getRentableProperty(int propertyId) {
        return propertyService.findById(propertyId)
                .filter(p -> p.getStatus() == PropertyStatus.FOR_RENT);
    }

    @Override
    public UrgentPaymentDto getUrgentPaymentDetails(int propertyId) {
        String title = propertyService.findById(propertyId)
                .map(PropertyResponseDto::getTitle)
                .orElse("Property");
        return UrgentPaymentDto.builder()
                .propertyId(propertyId)
                .propertyTitle(title)
                .amount(URGENT_PRICE)
                .build();
    }

    @Override
    @Transactional
    public void confirmBooking(int propertyId, BigDecimal amount, LocalDate startDate, LocalDate endDate, int guests, User user) {
        var property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));

        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDate.now());
        payment.setCreatedAt(LocalDate.now());
        payment.setUser(user);
        payment = paymentRepository.save(payment);

        Booking booking = new Booking();
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setProperty(property);
        booking.setPrice(amount.doubleValue());
        booking.setPayment(payment);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void confirmUrgent(int propertyId, User user) {
        var property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));

        UrgentSellPlan plan = urgentSellPlanRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new BusinessException(ErrorCode.URGENT_PLAN_NOT_FOUND));

        Payment payment = new Payment();
        payment.setAmount(URGENT_PRICE);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDate.now());
        payment.setCreatedAt(LocalDate.now());
        payment.setUser(user);
        payment = paymentRepository.save(payment);

        PropertyUrgent urgent = new PropertyUrgent();
        urgent.setProperty(property);
        urgent.setUrgentSellPlan(plan);
        urgent.setStartDate(LocalDate.now());
        urgent.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
        urgent.setActive(true);
        urgent.setCreatedAt(LocalDate.now());
        urgent.setPayment(payment);
        propertyUrgentRepository.save(urgent);
    }

    @Override
    public List<Integer> getActiveUrgentPropertyIds() {
        return propertyUrgentRepository.findActivePropertyIds();
    }
}