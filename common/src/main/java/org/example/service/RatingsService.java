package org.example.service;


import org.example.dto.ratings.RatingsDto;

import java.util.Optional;

public interface RatingsService {

    Optional<RatingsDto> findById(Integer id);

    Optional<RatingsDto> findByUserId(Integer userId);

    Optional<RatingsDto> findByPropertyId(Integer propertyId);

    RatingsDto save(RatingsDto ratingsDto);

    void deleteById(Integer id);

    RatingsDto update(RatingsDto ratingsDto);

}
