package org.example.dto.favorites;

import lombok.Data;
import org.example.model.Property;
import org.example.model.User;

import java.time.LocalDate;

@Data
public class FavoritesDto {
    private int id;
    private Property property;
    private User user;
    private LocalDate createdAt;
}
