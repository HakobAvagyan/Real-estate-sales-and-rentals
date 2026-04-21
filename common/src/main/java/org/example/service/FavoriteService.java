package org.example.service;

import org.example.dto.favorites.FavoritesDto;

import java.util.List;

public interface FavoriteService {

    List<FavoritesDto> findAllByUserId(Integer userId);

    void addFavoriteProperty(int propertyId,int  userId);

    boolean checkFavoriteProperty(int propertyId,int userId);

    void deleteByUserAndProperty(Integer favoritesId, int userId);

}
