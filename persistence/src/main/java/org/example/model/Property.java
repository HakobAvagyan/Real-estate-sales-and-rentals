package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private int surface;

    @Column(name = "rooms_count")
    private Integer roomsCount;

    @Column(name = "bathrooms_count")
    private Integer bathroomsCount;

    @Column(name = "floor_count")
    private Integer floorCount;

    @Column(nullable = false)
    private int floor;

    private BigDecimal price;

    @Column(name = "created_at")
    private LocalDate createdAt;

//    @ManyToOne
//    @JoinColumn(name = "location_id", nullable = false)
//    private Location location;
//
//    @Enumerated(EnumType.STRING)
//    private PropertyStatus status;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "type")
//    private PropertyType propertyType;
}
