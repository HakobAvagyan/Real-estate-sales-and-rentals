package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.AdDto;
import org.example.service.AdService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AdController {

    private final AdService adService;

    @GetMapping("/by-user")
    public List<AdDto> getByUser(@RequestParam int userId) {
        return adService.getAdsByUserId(userId);
    }

    @GetMapping("/{id}")
    public AdDto getById(@PathVariable int id) {
        return adService.getAdById(id);
    }

    @GetMapping
    public List<AdDto> getAllAds() {
        return adService.getAllAds();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdDto createAd(@RequestBody AdDto adDto) {
        return adService.createAd(adDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAd(@PathVariable int id) {
        adService.delete(id);
    }
}
