package org.example.mapper.favorites;

import org.example.dto.favorites.FavoritesDto;
import org.example.model.Favorites;

public class FavoritesMapper {

    public static FavoritesDto toFavoritesDto(Favorites favorites) {
        if (favorites == null) {
            return null;
        }
        FavoritesDto favoritesDto = new FavoritesDto();
        favoritesDto.setId(favorites.getId());
        favoritesDto.setProperty(favorites.getProperty());
        favoritesDto.setUser(favorites.getUser());
        favoritesDto.setCreatedAt(favorites.getCreatedAt());
        return favoritesDto;
    }

    public static Favorites toFavorites(FavoritesDto favoritesDto) {
        if (favoritesDto == null) {
            return null;
        }
        Favorites favorites = new Favorites();
        favorites.setId(favoritesDto.getId());
        favorites.setProperty(favoritesDto.getProperty());
        favorites.setUser(favoritesDto.getUser());
        favorites.setCreatedAt(favoritesDto.getCreatedAt());
        return favorites;
    }

}
