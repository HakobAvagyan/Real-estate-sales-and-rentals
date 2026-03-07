package org.example.service;

import org.example.dto.AdPlanDto;

import java.util.List;

public interface AdPlanService {
    AdPlanDto create(AdPlanDto dto);
    List<AdPlanDto> getAll();
    AdPlanDto getById(int id);
    void delete(int id);
}