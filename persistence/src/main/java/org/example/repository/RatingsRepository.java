package org.example.repository;

import org.example.model.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingsRepository extends JpaRepository<Ratings, Integer> {

    Optional<Ratings> findByUserId(Integer userId);

    Optional<Ratings> findByPropertyId(Integer propertyId);

}
