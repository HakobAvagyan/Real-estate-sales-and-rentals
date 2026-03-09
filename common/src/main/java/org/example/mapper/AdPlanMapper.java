package org.example.mapper;

import org.example.dto.AdPlanDto;
import org.example.model.AdPlan;

import java.time.LocalDate;


public class AdPlanMapper {
    public static AdPlanDto toDto(AdPlan entity) {
        if (entity == null) return null;

        AdPlanDto dto = new AdPlanDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setDurationDays(entity.getDurationDays());
        return dto;
    }

    public static AdPlan toEntity(AdPlanDto dto) {
        if (dto == null) return null;

        AdPlan entity = new AdPlan();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDurationDays(dto.getDurationDays());
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDate.now());
        }
        return entity;
    }
}
