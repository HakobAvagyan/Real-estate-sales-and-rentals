package org.example.dto.favorites;

import lombok.Data;
import org.example.dto.property.PropertyResponseDto;
import org.example.model.User;

@Data
public class FavoritesDto {
    private PropertyResponseDto property;
    private User user;
}
