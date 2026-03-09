package org.example.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.dto.AdDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.AdMapper;
import org.example.model.Ad;
import org.example.repository.AdRepository;
import org.example.service.AdService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {
    private final AdRepository adRepository;

    @Override
    @Transactional
    public AdDto createAd(AdDto dto) {
        Ad ad = AdMapper.toEntity(dto);
        Ad saveAd = adRepository.save(ad);
        return AdMapper.toDto(saveAd);
    }

    @Override
    public List<AdDto> getAllAds() {
        return adRepository.findAll().stream()
                .map(AdMapper::toDto)
                .toList();
    }

    @Override
    public AdDto getAdById(int id) {
        return adRepository.findById(id)
                .map(AdMapper::toDto)
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_NOT_FOUND, id));
    }

    @Override
    public void delete(int id) {
        adRepository.deleteById(id);
    }

    @Override
    public List<AdDto> getAdsByUserId(int userId) {
        return adRepository.findAll().stream()
                .filter(ad -> ad.getUser() != null && ad.getUser().getId() == userId)
                .map(AdMapper::toDto)
                .toList();
    }


}
