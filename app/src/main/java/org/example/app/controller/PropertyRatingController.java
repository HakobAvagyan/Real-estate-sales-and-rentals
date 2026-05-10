package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.exception.BusinessException;
import org.example.service.RatingsService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PropertyRatingController {

    private final RatingsService ratingsService;

    @PostMapping("/property/rating")
    public String submitRating(@AuthenticationPrincipal SpringUser userPrincipal,
                               @RequestParam int propertyId,
                               @RequestParam int stars,
                               @RequestParam(required = false) String reviewText,
                               RedirectAttributes redirectAttributes) {
        if (userPrincipal == null) {
            return "redirect:/loginPage";
        }
        try {
            ratingsService.upsertReview(userPrincipal.getUser().getId(), propertyId, stars, reviewText);
            redirectAttributes.addFlashAttribute("ratingSuccess", true);
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("ratingError", ex.getMessage());
        }
        return "redirect:/property/details?propertyId=" + propertyId;
    }
}
