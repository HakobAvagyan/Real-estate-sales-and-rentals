package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.AdPlanDto;
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
    private final AdPlanMapper adPlanMapper;

    @Override
    public AdPlanDto create(AdPlanDto dto) {
        AdPlan entity = adPlanMapper.toEntity(dto);
        AdPlan saved = adPlanRepository.save(entity);
        return adPlanMapper.toDto(saved);
    }

    @Override
    public List<AdPlanDto> getAll() {
        return adPlanRepository.findAll().stream()
                .map(adPlanMapper::toDto)
                .toList();
    }

    @Override
    public AdPlanDto getById(int id) {
        return adPlanRepository.findById(id)
                .map(adPlanMapper::toDto)
                .orElseThrow(() -> new RuntimeException("AdPlan not found with id: " + id));
    }

    @Override
    public void delete(int id) {
        adPlanRepository.deleteById(id);
    }
}
