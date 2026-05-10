package org.example.app.controller.favorite;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.favorites.FavoritePageDto;
import org.example.service.FavoriteService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@AllArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/favorite")
    public String getAllFavorite(ModelMap modelMap,
                                 @AuthenticationPrincipal SpringUser springUser) {
        FavoritePageDto favoritePageData =
                favoriteService.getFavoritePageData(springUser.getUser().getId());

        modelMap.addAttribute("favoritePageData", favoritePageData);
        return "favorite/favorite";
    }

    @PostMapping("/favorite/action")
    public String toggleFavorite(@RequestParam int propertyId, @AuthenticationPrincipal SpringUser springUser) {
        int userId = springUser.getUser().getId();

        if (favoriteService.checkFavoriteProperty(propertyId, userId)) {
            favoriteService.deleteByUserAndProperty(propertyId, userId);
            log.info("User {} removed property {} from favorites", userId, propertyId);
        } else {
            favoriteService.addFavoriteProperty(propertyId, userId);
            log.info("User {} added property {} to favorites", userId, propertyId);
        }

        return "redirect:/";
    }

}
