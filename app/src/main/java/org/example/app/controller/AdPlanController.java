package org.example.app.controller;

import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.AdPlanDto;
import org.example.service.AdPlanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AdPlanController {

    private final AdPlanService adPlanService;

    @GetMapping("/ad-plans")
    public String getAdPlans(ModelMap modelMap) {
        modelMap.addAttribute("adPlans", adPlanService.getAll());
        return "ad-plans";
    }

    @PostMapping("/ad-plans/add")
    public String addAdPlan(AdPlanDto adPlanDto, ModelMap modelMap) {
        adPlanService.create(adPlanDto);
        modelMap.addAttribute("adPlans", adPlanService.getAll());
        return "ad-plans";
    }

    @GetMapping("/ad-plans/delete")
    public String deleteAdPlan(@RequestParam int adPlanId, ModelMap modelMap) {
        adPlanService.delete(adPlanId);
        modelMap.addAttribute("adPlans", adPlanService.getAll());
        return "ad-plans";
    }

    @GetMapping("/ad-plans/update")
    public String updateAdPlan(AdPlanDto adPlanDto, ModelMap modelMap) {
        adPlanService.update(adPlanDto);
        modelMap.addAttribute("adPlans", adPlanService.getAll());
        return "ad-plans";
    }
}
