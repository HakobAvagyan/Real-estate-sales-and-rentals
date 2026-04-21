package org.example.app.controller.favorite;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.favorites.FavoritesDto;
import org.example.dto.location.LocationDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.example.model.enums.Region;
import org.example.service.FavoriteService;
import org.example.service.LocationService;
import org.example.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@AllArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final LocationService locationService;
    private final PaymentService paymentService;

    @GetMapping("/favorite")
    public String getAllFavorite(@RequestParam int userId, ModelMap modelMap) {
        List<FavoritesDto> favorites = favoriteService.findAllByUserId(userId);
        List<PropertyResponseDto> properties = favorites.stream()
                .map(FavoritesDto::getProperty)
                .toList();
        modelMap.addAttribute("properties", properties);
        modelMap.addAttribute("propertyTypes", PropertyType.values());
        modelMap.addAttribute("propertyStatuses", new PropertyStatus[]{PropertyStatus.FOR_SALE, PropertyStatus.FOR_RENT});
        modelMap.addAttribute("regions", Region.values());
        Map<Integer, LocationDto> locationMap = locationService.getAll()
                .stream().collect(Collectors.toMap(LocationDto::getId, l -> l));
        modelMap.addAttribute("locationMap", locationMap);
        modelMap.addAttribute("urgentPropertyIds", paymentService.getActiveUrgentPropertyIds());
        return "favorite/favorite";
    }

    @PostMapping("/favorite/action")
    public String addFavorite(@RequestParam int propertyId, @RequestParam int userId) {
        if(favoriteService.checkFavoriteProperty(propertyId,userId)){
            favoriteService.deleteByUserAndProperty(propertyId,userId);
            log.info("User with id {} removed property with id {} from favorites", userId, propertyId);
            return "redirect:/";
        }
        favoriteService.addFavoriteProperty(propertyId,userId);
        log.info("User with id {} added property with id {} to favorites", userId, propertyId);
        return "redirect:/";
    }

}
