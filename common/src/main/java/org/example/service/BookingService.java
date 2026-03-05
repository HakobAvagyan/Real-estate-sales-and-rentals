package org.example.service;

import org.example.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    Booking save(Booking booking);

    Booking update(Booking booking);

    List<Booking> findAll();

    Optional<Booking> findById(int id);

    void deleteById(int id);

    List<Booking> findByPropertyId(int propertyId);
}
