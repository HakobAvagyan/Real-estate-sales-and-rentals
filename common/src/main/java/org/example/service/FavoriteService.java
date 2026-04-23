package org.example.service;

import org.example.dto.favorites.FavoritePageDto;

public interface FavoriteService {

    FavoritePageDto getFavoritePageData(int userId);

    void addFavoriteProperty(int propertyId,int  userId);

    boolean checkFavoriteProperty(int propertyId,int userId);

    void deleteByUserAndProperty(Integer favoritesId, int userId);

}
