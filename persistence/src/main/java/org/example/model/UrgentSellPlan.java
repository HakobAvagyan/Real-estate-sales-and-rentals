package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "urgent_sell_plane")
public class UrgentSellPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "duration_days")
    private int durationDays;

    @Column(name = "created_at")
    private LocalDate createdAt;

    private String name;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "update_at")
    private LocalDate updateAt;
}
