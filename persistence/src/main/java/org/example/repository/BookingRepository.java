package org.example.repository;

import org.example.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.property.id = :propertyId " +
            "AND b.id != :id " +
            "AND (:start < b.endDate AND :end > b.startDate)")
    boolean existsOverlapping(
            @Param("propertyId") int propertyId,
            @Param("id") int id,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
    List<Booking> findAllByPropertyId(int propertyId);
}
