package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.favorites.FavoritesDto;
import org.example.dto.notification.NotificationRequestDto;
import org.example.dto.user.UserRegisterDto;
import org.example.dto.user.UserResponseDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.favorites.FavoritesMapper;
import org.example.mapper.user.UserResponseMapper;
import org.example.model.Favorites;
import org.example.model.User;
import org.example.model.enums.NotificationType;
import org.example.repository.FavoritesRepository;
import org.example.service.FavoriteService;
import org.example.service.NotificationService;
import org.example.service.PropertyService;
import org.example.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl implements FavoriteService {

    private final FavoritesRepository favoritesRepository;
    private final UserService userService;
    private final PropertyService propertyService;
    private final UserResponseMapper userResponseMapper;
    private final NotificationService notificationService;
    private final FavoritesMapper favoritesMapper;

    @Override
    public List<FavoritesDto> findAllByUserId(Integer userId) {
        userService.findById(userId);
        return favoritesMapper.toFavoritesListDto(favoritesRepository.findAllByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FAVORITES_NOT_FOUND, userId)));
    }

    @Override
    @Transactional
    public void addFavoriteProperty(int propertyId, int userId) {
        UserResponseDto user = userService.findById(userId);
        propertyService.findById(propertyId).ifPresent(property -> {
            FavoritesDto favorites = new FavoritesDto();
            favorites.setUser(userResponseMapper.toUser(user));
            favorites.setProperty(property);
            favoritesRepository.save(favoritesMapper.toFavorites(favorites));

            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setUserId(user.getId());
            notificationRequestDto.setTitle("You added your property to Favorite Property List successfully!");
            notificationRequestDto.setMessage(NotificationType.PROPERTY_ADD_FAVORITE_LIST_NOTIFICATION
                    .format(user.getName(),user.getSurname()));
            notificationService.save(notificationRequestDto);
        });
    }

    @Override
    public boolean checkFavoriteProperty(int propertyId, int userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            UserRegisterDto user = userService.findByEmail(auth.getName());
            if (user.getId() != userId) {
                log.error("Unauthorized access attempt to check favorite property user: {}, userId: {}", auth.getName(), userId);
               throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED, userId);
            }
        }
        return favoritesRepository.existsByUserIdAndPropertyId(userId, propertyId);
    }


    @Override
    @Transactional
    public void deleteByUserAndProperty(Integer propertyId, int userId) {
        User user = userResponseMapper.toUser(userService.findById(userId));
        Favorites favorite  = favoritesRepository.findFavoriteByUserAndPropertyId(user, propertyId).orElseThrow(
                () -> new BusinessException(ErrorCode.FAVORITES_NOT_FOUND_WHIT_USER_ID_OR_PROPERTY_ID, userId, propertyId));
        favoritesRepository.deleteById(favorite.getId());
            NotificationRequestDto notificationRequestDto = new NotificationRequestDto();
            notificationRequestDto.setUserId(user.getId());
            notificationRequestDto.setTitle("You removed your property from Favorite Property List successfully!");
            notificationRequestDto.setMessage(NotificationType.PROPERTY_REMOVE_FAVORITE_LIST_NOTIFICATION
                    .format(user.getName(),user.getSurname()));
            notificationService.save(notificationRequestDto);
    }

}
