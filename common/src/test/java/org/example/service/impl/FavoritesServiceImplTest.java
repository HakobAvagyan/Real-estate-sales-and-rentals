package org.example.service.impl;

import org.example.dto.favorites.FavoritesDto;
import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.dto.user.UserResponseDto;
import org.example.exception.BusinessException;
import org.example.mapper.favorites.FavoritesMapper;
import org.example.mapper.user.UserResponseMapper;
import org.example.model.Favorites;
import org.example.model.User;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.example.repository.FavoritesRepository;
import org.example.service.LocationService;
import org.example.service.NotificationService;
import org.example.service.PaymentService;
import org.example.service.PropertyService;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoritesServiceImplTest {

    @Mock FavoritesRepository favoritesRepository;
    @Mock UserService userService;
    @Mock PropertyService propertyService;
    @Mock LocationService locationService;
    @Mock PaymentService paymentService;
    @Mock UserResponseMapper userResponseMapper;
    @Mock NotificationService notificationService;
    @Mock FavoritesMapper favoritesMapper;

    @InjectMocks
    FavoritesServiceImpl favoritesService;

    @Test
    void addFavoriteProperty_savesAndNotifies() {
        int userId = 1;
        int propertyId = 10;

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(userId);
        userDto.setName("Test");
        userDto.setSurname("User");

        PropertyResponseDto propertyDto = PropertyResponseDto.builder()
                .id(propertyId).userId(2).locationId(1).title("Test Property")
                .price(BigDecimal.valueOf(100000)).surface(50)
                .status(PropertyStatus.FOR_SALE).propertyType(PropertyType.APARTMENT)
                .createdAt(LocalDate.now()).build();

        User userEntity = new User();
        userEntity.setId(userId);
        Favorites savedFavorite = new Favorites();

        when(userService.findById(userId)).thenReturn(userDto);
        when(propertyService.findById(propertyId)).thenReturn(Optional.of(propertyDto));
        when(userResponseMapper.toUser(userDto)).thenReturn(userEntity);
        when(favoritesMapper.toFavorites(any(FavoritesDto.class))).thenReturn(savedFavorite);
        when(favoritesRepository.save(savedFavorite)).thenReturn(savedFavorite);

        favoritesService.addFavoriteProperty(propertyId, userId);

        verify(favoritesRepository).save(savedFavorite);
        ArgumentCaptor<NotificationRequestDto> notifCaptor = ArgumentCaptor.forClass(NotificationRequestDto.class);
        verify(notificationService).save(notifCaptor.capture());
        assertEquals(userId, notifCaptor.getValue().getUserId());
    }

    @Test
    void deleteByUserAndProperty_throwsWhenNotFound() {
        int userId = 1;
        int propertyId = 10;

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(userId);
        User userEntity = new User();
        userEntity.setId(userId);

        when(userService.findById(userId)).thenReturn(userDto);
        when(userResponseMapper.toUser(userDto)).thenReturn(userEntity);
        when(favoritesRepository.findFavoriteByUserAndPropertyId(userEntity, propertyId))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> favoritesService.deleteByUserAndProperty(propertyId, userId));
    }
}
