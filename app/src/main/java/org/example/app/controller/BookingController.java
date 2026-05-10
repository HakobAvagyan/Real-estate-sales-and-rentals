package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.service.BookingService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/bookings/my")
    public String myBookings(@AuthenticationPrincipal SpringUser userPrincipal, ModelMap modelMap) {
        modelMap.addAttribute("bookings", bookingService.findByUserId(userPrincipal.getUser().getId()));
        return "booking/myBookings";
    }

    @GetMapping("/bookings/delete")
    public String deleteBooking(@RequestParam int bookingId,
                                @AuthenticationPrincipal SpringUser userPrincipal) {
        bookingService.deleteById(bookingId);
        return "redirect:/bookings/my";
    }
}