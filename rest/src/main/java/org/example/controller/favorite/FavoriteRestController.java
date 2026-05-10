package org.example.controller.favorite;

import lombok.RequiredArgsConstructor;
import org.example.dto.favorites.FavoritePageDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.service.FavoriteService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteRestController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    private int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        return userService.findByEmail(authentication.getName()).getId();
    }

    @GetMapping
    public FavoritePageDto getFavorites() {
        return favoriteService.getFavoritePageData(getCurrentUserId());
    }

    @PostMapping("/{propertyId}")
    public ResponseEntity<Void> toggleFavorite(@PathVariable int propertyId) {
        int userId = getCurrentUserId();
        if (favoriteService.checkFavoriteProperty(propertyId, userId)) {
            favoriteService.deleteByUserAndProperty(propertyId, userId);
        } else {
            favoriteService.addFavoriteProperty(propertyId, userId);
        }
        return ResponseEntity.ok().build();
    }
}
