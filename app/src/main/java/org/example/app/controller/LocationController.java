package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.location.LocationDto;
import org.example.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/locations")
    public String getLocations(ModelMap modelMap) {
        modelMap.addAttribute("locations", locationService.getAll());
        return "locations";
    }

    @PostMapping("/locations/add")
    public String addLocation(LocationDto locationDto, ModelMap modelMap) {
        locationService.save(locationDto);
        modelMap.addAttribute("locations", locationService.getAll());
        return "locations";
    }

    @GetMapping("/locations/delete")
    public String deleteLocationByID(@RequestParam int locationId, ModelMap modelMap) {
        locationService.deleteByID(locationId);
        return "redirect:/locations";
    }

    @GetMapping("/locations/update")
    public String updateLocation(LocationDto locationDto, ModelMap modelMap) {
        locationService.update(locationDto);
        modelMap.addAttribute("locations", locationService.getAll());
        return "redirect:/locations";
    }
}
