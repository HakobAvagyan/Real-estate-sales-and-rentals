package org.example.mapper;

import org.example.dto.AdDto;
import org.example.model.Ad;
import org.example.model.AdPlan;
import org.example.model.User;


public class AdMapper {
    public static AdDto toDto(Ad entity) {
        if (entity == null) return null;
        AdDto dto = new AdDto();
        dto.setId(entity.getId());
        dto.setImagesUrl(entity.getImagesUrl());
        dto.setVideoUrl(entity.getVideoUrl());
        dto.setPayed(entity.isPayed());
        if (entity.getUser() != null) dto.setUserId(entity.getUser().getId());
        if (entity.getAdPlan() != null) dto.setAdPlanId(entity.getAdPlan().getId());
        return dto;
    }

    public static Ad toEntity(AdDto dto) {
        if (dto == null) {
            return null;
        }

        Ad entity = new Ad();
        entity.setId(dto.getId());
        entity.setImagesUrl(dto.getImagesUrl());
        entity.setVideoUrl(dto.getVideoUrl());
        entity.setPayed(dto.isPayed());
        entity.setBlocked(false);
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
        return entity;
    }
}
