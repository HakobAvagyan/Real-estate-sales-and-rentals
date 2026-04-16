package org.example.mapper.ratings;

import org.example.dto.ratings.RatingsDto;
import org.example.model.Ratings;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RatingsMapper {

    RatingsDto toRatingsDto(Ratings ratings);

    Ratings toRatings(RatingsDto ratingsDto);
}
