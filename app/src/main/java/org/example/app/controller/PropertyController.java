package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.property.PropertyCreateRequestDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.example.service.LocationService;
import org.example.service.PropertyService;
import org.example.service.security.SpringUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final LocationService locationService;

    @GetMapping("/user/property/create")
    public String createPropertyPage(ModelMap modelMap) {
        modelMap.addAttribute("locationNames", locationService.getAllLocationNames());
        modelMap.addAttribute("propertyTypes", PropertyType.values());
        modelMap.addAttribute("propertyStatuses", PropertyStatus.values());
        return "property/createProperty";
    }

    @PostMapping("/user/property/create")
    public String addProperty(@ModelAttribute PropertyCreateRequestDto requestDto,
                              @RequestParam(value = "images", required = false) List<MultipartFile> images,
                              @AuthenticationPrincipal SpringUser principal) {
        requestDto.setUserId(principal.getUser().getId());
        PropertyResponseDto createdProperty = propertyService.create(requestDto, images);
        return "redirect:/user/property/create?successId=" + createdProperty.getId();
    }
}
