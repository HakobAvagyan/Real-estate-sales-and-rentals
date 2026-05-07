package org.example.mapper.ratings;

import org.example.dto.ratings.RatingsDto;
import org.example.model.Ratings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingsMapper {

    @Mapping(target = "propertyId", source = "property.id")
    @Mapping(target = "userId", source = "user.id")
    RatingsDto toRatingsDto(Ratings ratings);

    @Mapping(target = "property", ignore = true)
    @Mapping(target = "user", ignore = true)
    Ratings toRatings(RatingsDto ratingsDto);
}
