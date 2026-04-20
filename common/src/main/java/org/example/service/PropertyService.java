package org.example.service;

import org.example.dto.property.PropertyCreateRequestDto;
import org.example.dto.property.PropertyFilterDto;
import org.example.dto.property.PropertyResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PropertyService {
    PropertyResponseDto addProperty(PropertyCreateRequestDto request, List<MultipartFile> images);
    List<PropertyResponseDto> findAll();
    List<PropertyResponseDto> findAllFiltered(PropertyFilterDto filter);
    List<PropertyResponseDto> findAllByUserId(Integer userId);
    Optional<PropertyResponseDto> findById(int id);

    default PropertyResponseDto create(PropertyCreateRequestDto request, List<MultipartFile> images) {
        return addProperty(request, images);
    }
}
