package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.AdPlanDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.AdPlanMapper;
import org.example.model.AdPlan;
import org.example.repository.AdPlanRepository;
import org.example.service.AdPlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdPlanServiceImpl implements AdPlanService {

    private final AdPlanRepository adPlanRepository;

    @Override
    public AdPlanDto create(AdPlanDto dto) {
        AdPlan entity = AdPlanMapper.toEntity(dto);
        AdPlan saved = adPlanRepository.save(entity);
        return AdPlanMapper.toDto(saved);
    }
    
    @Override
    public AdPlanDto update(AdPlanDto dto) {
        AdPlan entity = AdPlanMapper.toEntity(dto);
        AdPlan updated = adPlanRepository.save(entity);
        return AdPlanMapper.toDto(updated);
    }

    @Override
    public List<AdPlanDto> getAll() {
        return adPlanRepository.findAll().stream()
                .map(AdPlanMapper::toDto)
                .toList();
    }

    @Override
    public AdPlanDto getById(int id) {
        return adPlanRepository.findById(id)
                .map(AdPlanMapper::toDto)
                .orElseThrow(() -> new BusinessException(ErrorCode.AD_PLAN_NOT_FOUND, id));
    }

    @Override
    public void delete(int id) {
        adPlanRepository.deleteById(id);
    }
}
