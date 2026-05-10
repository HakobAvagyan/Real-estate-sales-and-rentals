package org.example.service.impl;

import org.example.model.Booking;
import org.example.model.Property;
import org.example.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    private Booking sampleBooking(int id) {
        Booking b = new Booking();
        b.setId(id);
        b.setStartDate(LocalDate.of(2025, 1, 10));
        b.setEndDate(LocalDate.of(2025, 1, 20));
        b.setPrice(500.0);
        return b;
    }

    @Test
    void save_persistsAndReturnsBooking() {
        Booking booking = sampleBooking(0);
        Booking saved = sampleBooking(1);
        when(bookingRepository.save(booking)).thenReturn(saved);

        Booking result = bookingService.save(booking);

        assertEquals(1, result.getId());
        verify(bookingRepository).save(booking);
    }

    @Test
    void update_savesBookingAndReturns() {
        Booking booking = sampleBooking(5);
        when(bookingRepository.save(booking)).thenReturn(booking);

        Booking result = bookingService.update(booking);

        assertSame(booking, result);
        verify(bookingRepository).save(booking);
    }

    @Test
    void findAll_returnsAllBookings() {
        List<Booking> bookings = List.of(sampleBooking(1), sampleBooking(2));
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.findAll();

        assertEquals(2, result.size());
        verify(bookingRepository).findAll();
    }

    @Test
    void findById_returnsOptionalWithBooking() {
        Booking booking = sampleBooking(3);
        when(bookingRepository.findById(3)).thenReturn(Optional.of(booking));

        Optional<Booking> result = bookingService.findById(3);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().getId());
    }

    @Test
    void findById_returnsEmptyWhenNotFound() {
        when(bookingRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Booking> result = bookingService.findById(99);

        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_callsRepositoryDelete() {
        bookingService.deleteById(7);
        verify(bookingRepository).deleteById(7);
    }

    @Test
    void findByPropertyId_returnsBookingsForProperty() {
        Booking b1 = sampleBooking(1);
        Booking b2 = sampleBooking(2);
        when(bookingRepository.findAllByPropertyId(10)).thenReturn(List.of(b1, b2));

        List<Booking> result = bookingService.findByPropertyId(10);

        assertEquals(2, result.size());
        verify(bookingRepository).findAllByPropertyId(10);
    }

    @Test
    void findByUserId_returnsBookingsForUser() {
        Booking b = sampleBooking(1);
        when(bookingRepository.findAllByPayment_UserId(42)).thenReturn(List.of(b));

        List<Booking> result = bookingService.findByUserId(42);

        assertEquals(1, result.size());
        verify(bookingRepository).findAllByPayment_UserId(42);
    }

    @Test
    void findByPropertyId_returnsEmptyListWhenNone() {
        when(bookingRepository.findAllByPropertyId(999)).thenReturn(List.of());

        List<Booking> result = bookingService.findByPropertyId(999);

        assertTrue(result.isEmpty());
    }
}
