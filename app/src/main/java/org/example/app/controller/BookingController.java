package org.example.app.controller;
import lombok.RequiredArgsConstructor;
import org.example.model.Booking;
import org.example.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    
    @GetMapping("/bookings")
    public String getBookings(ModelMap modelMap) {
        modelMap.addAttribute("bookings", bookingService.findAll());
        return "bookings";
    }

    @PostMapping("/bookings/add")
    public String addBooking(Booking booking, ModelMap modelMap) {
        bookingService.save(booking);
        modelMap.addAttribute("bookings", bookingService.findAll());
        return "bookings";
    }

    @GetMapping("/bookings/delete")
    public String deleteBooking(@RequestParam int bookingId) {
        bookingService.deleteById(bookingId);
        return "redirect:/bookings";
    }

    @GetMapping("/bookings/by-property")
    public String getByProperty(@RequestParam int propertyId, ModelMap modelMap) {
        modelMap.addAttribute("bookings", bookingService.findByPropertyId(propertyId));
        return "bookings";
    }

}