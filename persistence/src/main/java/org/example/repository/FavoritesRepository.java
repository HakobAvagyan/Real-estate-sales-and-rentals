package org.example.repository;

import org.example.model.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {

    Optional<Favorites> findByUserId(Integer userId);

    Optional<Favorites> findByPropertyId(Integer propertyId);

}
