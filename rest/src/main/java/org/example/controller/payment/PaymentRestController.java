package org.example.controller.payment;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, 0));
    }

    @PostMapping("/booking")
    public ResponseEntity<Void> confirmBookingPayment(
            @RequestParam int propertyId,
            @RequestParam BigDecimal amount,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false, defaultValue = "1") int guests) {
        User user = getCurrentUser();
        paymentService.confirmBooking(propertyId, amount, LocalDate.parse(startDate), LocalDate.parse(endDate), guests, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/urgent")
    public ResponseEntity<Void> confirmUrgentPayment(@RequestParam int propertyId) {
        User user = getCurrentUser();
        paymentService.confirmUrgent(propertyId, user);
        return ResponseEntity.ok().build();
    }
}
