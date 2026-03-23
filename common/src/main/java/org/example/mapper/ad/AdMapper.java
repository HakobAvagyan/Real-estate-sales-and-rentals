package org.example.mapper.ad;

import org.example.dto.AdDto;
import org.example.model.Ad;
import org.example.model.AdPlan;
import org.example.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class AdMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "adPlanId", source = "adPlan.id")
    public abstract AdDto toDto(Ad entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "adPlan", ignore = true)
    @Mapping(target = "blocked", constant = "false")
    public abstract Ad toEntity(AdDto dto);

    @AfterMapping
    protected void afterToEntity(AdDto dto, @MappingTarget Ad entity) {
        if (dto.getUserId() != 0) {
            User user = new User();
            user.setId(dto.getUserId());
            entity.setUser(user);
        }
        if (dto.getAdPlanId() != 0) {
            AdPlan adPlan = new AdPlan();
            adPlan.setId(dto.getAdPlanId());
            entity.setAdPlan(adPlan);
        }
    }
}
