package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.payment.UrgentPaymentDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.model.User;
import org.example.service.PaymentService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/booking/form")
    public String bookingForm(@RequestParam int propertyId, ModelMap model) {
        Optional<PropertyResponseDto> property = paymentService.getRentableProperty(propertyId);
        if (property.isEmpty()) {
            return "redirect:/home";
        }
        model.addAttribute("property", property.get());
        return "booking/bookingForm";
    }

    @PostMapping("/booking/checkout")
    public String bookingCheckout(
            @RequestParam int propertyId,
            @RequestParam String propertyTitle,
            @RequestParam BigDecimal amount,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam int guests) {

        String redirectUrl = UriComponentsBuilder.fromPath("/payment/form")
                .queryParam("type", "BOOKING")
                .queryParam("amount", amount)
                .queryParam("propertyId", propertyId)
                .queryParam("propertyTitle", propertyTitle)
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .queryParam("guests", guests)
                .build().toUriString();
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/payment/urgent")
    public String urgentPayment(@RequestParam int propertyId) {
        UrgentPaymentDto dto = paymentService.getUrgentPaymentDetails(propertyId);
        String redirectUrl = UriComponentsBuilder.fromPath("/payment/form")
                .queryParam("type", "URGENT")
                .queryParam("amount", dto.getAmount())
                .queryParam("propertyId", dto.getPropertyId())
                .queryParam("propertyTitle", dto.getPropertyTitle())
                .build().toUriString();
        return "redirect:" + redirectUrl;
    }

    @GetMapping("/payment/form")
    public String paymentForm(
            @RequestParam String type,
            @RequestParam BigDecimal amount,
            @RequestParam int propertyId,
            @RequestParam String propertyTitle,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "1") int guests,
            ModelMap model) {

        model.addAttribute("type", type);
        model.addAttribute("amount", amount);
        model.addAttribute("propertyId", propertyId);
        model.addAttribute("propertyTitle", propertyTitle);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("guests", guests);
        return "payment/paymentForm";
    }

    @PostMapping("/payment/confirm")
    public String paymentConfirm(
            @RequestParam String type,
            @RequestParam int propertyId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "1") int guests,
            @AuthenticationPrincipal SpringUser springUser) {

        User user = springUser.getUser();
        if ("BOOKING".equals(type)) {
            paymentService.confirmBooking(propertyId, amount,
                    LocalDate.parse(startDate), LocalDate.parse(endDate), guests, user);
        } else if ("URGENT".equals(type)) {
            paymentService.confirmUrgent(propertyId, user);
        }
        return "redirect:/home";
    }
}