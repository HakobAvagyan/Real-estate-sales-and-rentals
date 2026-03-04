package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "rented_start_date")
    private LocalDate startDate;

    @Column(name = "rented_end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    private double price;

    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
