package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.property.Property360Dto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.Property;
import org.example.model.Property360;
import org.example.repository.Property360Repository;
import org.example.repository.PropertyRepository;
import org.example.service.Property360Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Property360ServiceImpl implements Property360Service {

    private final Property360Repository property360Repository;
    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public Property360Dto addOrUpdate(int propertyId, String viewUrl, int currentUserId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));
        if (property.getUser().getId() != currentUserId) {
            throw new BusinessException(ErrorCode.PROFILE_EDIT_NOT_ALLOWED);
        }
        Property360 view360 = property360Repository.findByPropertyId(propertyId)
                .orElse(new Property360());
        view360.setProperty(property);
        view360.setViewUrl(viewUrl);
        Property360 saved = property360Repository.save(view360);
        return toDto(saved);
    }

    @Override
    public Optional<Property360Dto> getByPropertyId(int propertyId) {
        return property360Repository.findByPropertyId(propertyId).map(this::toDto);
    }

    @Override
    @Transactional
    public void delete(int propertyId, int currentUserId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));
        if (property.getUser().getId() != currentUserId) {
            throw new BusinessException(ErrorCode.PROFILE_EDIT_NOT_ALLOWED);
        }
        property360Repository.deleteByPropertyId(propertyId);
    }

    private Property360Dto toDto(Property360 entity) {
        Property360Dto dto = new Property360Dto();
        dto.setId(entity.getId());
        dto.setPropertyId(entity.getProperty().getId());
        dto.setViewUrl(entity.getViewUrl());
        return dto;
    }
}