package org.example.app.controller.ad;

import lombok.RequiredArgsConstructor;
import org.example.dto.AdDto;
import org.example.service.AdService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @GetMapping("/ads")
    public String getAds(ModelMap modelMap) {
        modelMap.addAttribute("ads", adService.getAllAds());
        return "ads";
    }

    @PostMapping("/ads/add")
    public String addAd(AdDto adDto, ModelMap modelMap) {
        adService.createAd(adDto);
        modelMap.addAttribute("ads", adService.getAllAds());
        return "ads";
    }

    @GetMapping("/ads/delete")
    public String deleteAd(@RequestParam int adId, ModelMap modelMap) {
        adService.delete(adId);
        modelMap.addAttribute("ads", adService.getAllAds());
        return "ads";
    }

    @GetMapping("/ads/by-user")
    public String getAdsByUser(@RequestParam int userId, ModelMap modelMap) {
        modelMap.addAttribute("ads", adService.getAdsByUserId(userId));
        return "ads";
    }
}
