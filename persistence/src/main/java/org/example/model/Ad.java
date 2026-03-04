package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ad")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "images_url")
    private String imagesUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "is_payed")
    private boolean isPayed;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ad_plans_id")
    private AdPlan adPlan;
}
