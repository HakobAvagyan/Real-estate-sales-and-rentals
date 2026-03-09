package org.example.service;

import org.example.dto.AdDto;

import java.util.List;

public interface AdService {
    AdDto createAd(AdDto dto);

    List<AdDto> getAllAds();

    AdDto getAdById(int id);

    void delete(int id);

    List<AdDto> getAdsByUserId(int userId);
}