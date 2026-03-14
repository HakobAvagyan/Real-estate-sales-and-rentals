package org.example.service;

import org.example.dto.favorites.FavoritesDto;

import java.util.Optional;

public interface FavoritesService {

    Optional<FavoritesDto> findById(Integer id);

    Optional<FavoritesDto> findByUserId(Integer userId);

    Optional<FavoritesDto> findByPropertyId(Integer propertyId);

    FavoritesDto save(FavoritesDto favoritesDto);

    void deleteById(Integer id);

    FavoritesDto update(FavoritesDto favoritesDto);

}
