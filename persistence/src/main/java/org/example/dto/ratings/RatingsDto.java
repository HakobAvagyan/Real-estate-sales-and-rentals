package org.example.dto.ratings;

import lombok.Data;
import org.example.model.Property;
import org.example.model.User;

@Data
public class RatingsDto {
    private int id;
    private int rating;
    private Property property;
    private User user;
}
