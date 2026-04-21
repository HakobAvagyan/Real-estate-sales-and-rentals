package org.example.mapper.favorites;

import org.example.dto.favorites.FavoritesDto;
import org.example.model.Favorites;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FavoritesMapper {

    FavoritesDto toFavoritesDto(Favorites favorites);

    List<FavoritesDto> toFavoritesListDto(List<Favorites> favorites);

    Favorites toFavorites(FavoritesDto favoritesDto);
}
