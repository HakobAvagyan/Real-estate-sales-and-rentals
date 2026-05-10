package org.example.controller.booking;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.Booking;
import org.example.service.BookingService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingRestController {

    private final BookingService bookingService;
    private final UserService userService;

    private int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        return userService.findByEmail(authentication.getName()).getId();
    }

    @GetMapping("/my")
    public List<Booking> myBookings() {
        return bookingService.findByUserId(getCurrentUserId());
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable int bookingId) {
        bookingService.deleteById(bookingId);
        return ResponseEntity.ok().build();
    }
}
