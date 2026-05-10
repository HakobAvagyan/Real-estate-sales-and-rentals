package org.example.repository;

import org.example.model.Favorites;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Integer> {

    Optional<List<Favorites>> findAllByUserId(Integer userId);

    Optional<Favorites> findFavoriteByUserAndPropertyId(User user, int property_id);

    boolean existsByUserIdAndPropertyId(Integer userId, Integer propertyId);


}
