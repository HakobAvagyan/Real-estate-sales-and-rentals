package org.example.mapper.ad;

import org.example.dto.AdPlanDto;
import org.example.model.AdPlan;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AdPlanMapper {

    public abstract AdPlanDto toDto(AdPlan entity);

    public abstract AdPlan toEntity(AdPlanDto dto);

    @AfterMapping
    protected void afterToEntity(@MappingTarget AdPlan entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDate.now());
        }
    }
}
