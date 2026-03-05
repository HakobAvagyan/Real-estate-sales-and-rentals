package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Booking;
import org.example.repository.BookingRepository;
import org.example.service.BookingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public Booking save(Booking booking) {
        if (bookingRepository.existsOverlapping(
                booking.getProperty().getId(),
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate())) {
            throw new IllegalArgumentException("Booking dates overlap with an existing booking for the same property.");
        }
        return bookingRepository.save(booking);
    }


    @Override
    public Booking update(Booking booking) {
        if (bookingRepository.existsOverlapping(
                booking.getProperty().getId(),
                booking.getId(),
                booking.getStartDate(),
                booking.getEndDate())) {
            throw new IllegalArgumentException("Booking dates overlap with an existing booking for the same property.");
        }
        return bookingRepository.save(booking);
    }


    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> findById(int id) {
        return bookingRepository.findById(id);
    }

    @Override
    public void deleteById(int id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public List<Booking> findByPropertyId(int propertyId) {
        return bookingRepository.findAllByPropertyId(propertyId);
    }
}
