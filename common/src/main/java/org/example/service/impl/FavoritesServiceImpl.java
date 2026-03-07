package org.example.service.impl;

import lombok.RequiredArgsConstructor;
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
    public Optional<Favorites> findById(Integer id) {
        return favoritesRepository.findById(id);
    }

    @Override
    public Optional<Favorites> findByUserId(Integer userId) {
        return favoritesRepository.findByUserId(userId);
    }

    @Override
    public Optional<Favorites> findByPropertyId(Integer propertyId) {
        return favoritesRepository.findByPropertyId(propertyId);
    }

    @Override
    public void save(Favorites favorites) {
        favoritesRepository.save(favorites);
    }

    @Override
    public void deleteById(Integer id) {
        favoritesRepository.deleteById(id);
    }

    @Override
    public Favorites update(Favorites favorites) {
        return favoritesRepository.save(favorites);
    }
}
