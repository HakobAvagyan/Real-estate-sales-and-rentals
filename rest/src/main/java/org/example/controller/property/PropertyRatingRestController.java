package org.example.controller.property;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.service.RatingsService;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/properties/{propertyId}/ratings")
@RequiredArgsConstructor
public class PropertyRatingRestController {

    private final RatingsService ratingsService;
    private final UserService userService;

    private int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        return userService.findByEmail(authentication.getName()).getId();
    }

    @PostMapping
    public ResponseEntity<Void> submitRating(
            @PathVariable int propertyId,
            @RequestParam int stars,
            @RequestParam(required = false) String reviewText) {
        int userId = getCurrentUserId();
        ratingsService.upsertReview(userId, propertyId, stars, reviewText);
        return ResponseEntity.ok().build();
    }
}
