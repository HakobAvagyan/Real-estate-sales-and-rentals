package org.example.service;


import org.example.model.Ratings;

import java.util.Optional;

public interface RatingsService {

    Optional<Ratings> findById(Integer id);

    Optional<Ratings> findByUserId(Integer userId);

    Optional<Ratings> findByPropertyId(Integer propertyId);

    void save(Ratings ratings);

    void deleteById(Integer id);

    Ratings update(Ratings ratings);

}
