package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.favorites.FavoritesDto;
import org.example.mapper.favorites.FavoritesMapper;
import org.example.model.Favorites;
import org.example.repository.FavoritesRepository;
import org.example.service.FavoritesService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritesRepository favoritesRepository;

    @Override
    public Optional<FavoritesDto> findById(Integer id) {
        return favoritesRepository.findById(id).map(FavoritesMapper::toFavoritesDto);
    }

    @Override
    public Optional<FavoritesDto> findByUserId(Integer userId) {
        return favoritesRepository.findByUserId(userId).map(FavoritesMapper::toFavoritesDto);
    }

    @Override
    public Optional<FavoritesDto> findByPropertyId(Integer propertyId) {
        return favoritesRepository.findByPropertyId(propertyId).map(FavoritesMapper::toFavoritesDto);
    }

    @Override
    public FavoritesDto save(FavoritesDto favoritesDto) {
        Favorites favorites = FavoritesMapper.toFavorites(favoritesDto);
        favoritesRepository.save(favorites);
        return FavoritesMapper.toFavoritesDto(favorites);
    }

    @Override
    public void deleteById(Integer id) {
        favoritesRepository.deleteById(id);
    }

    @Override
    public FavoritesDto update(FavoritesDto favoritesDto) {
        Favorites favorites = FavoritesMapper.toFavorites(favoritesDto);
        favoritesRepository.save(favorites);
        return FavoritesMapper.toFavoritesDto(favorites);
    }
}
