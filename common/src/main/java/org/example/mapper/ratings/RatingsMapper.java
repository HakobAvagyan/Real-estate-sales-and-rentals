package org.example.mapper.ratings;

import org.example.dto.ratings.RatingsDto;
import org.example.model.Ratings;

public class RatingsMapper {
    public static RatingsDto toRatingsDto(Ratings ratings) {
        if (ratings == null) {
            return null;
        }
        RatingsDto ratingsDto = new RatingsDto();
        ratingsDto.setId(ratings.getId());
        ratingsDto.setRating(ratings.getRating());
        ratingsDto.setProperty(ratings.getProperty());
        ratingsDto.setUser(ratings.getUser());
        return ratingsDto;
    }

    public static Ratings toRatings(RatingsDto ratingsDto) {
        if (ratingsDto == null) {
            return null;
        }
        Ratings ratings = new Ratings();
        ratings.setId(ratingsDto.getId());
        ratings.setRating(ratingsDto.getRating());
        ratings.setProperty(ratingsDto.getProperty());
        ratings.setUser(ratingsDto.getUser());
        return ratings;
    }
}
