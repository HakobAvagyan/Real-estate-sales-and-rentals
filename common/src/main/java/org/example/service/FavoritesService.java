package org.example.service;

import org.example.model.Favorites;
import java.util.Optional;

public interface FavoritesService {

    Optional<Favorites> findById(Integer id);

    Optional<Favorites> findByUserId(Integer userId);

    Optional<Favorites> findByPropertyId(Integer propertyId);

    void save(Favorites favorites);

    void deleteById(Integer id);

    Favorites update(Favorites favorites);

}
