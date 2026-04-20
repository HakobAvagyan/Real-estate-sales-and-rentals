package org.example.service;

import org.example.dto.property.PropertyCreateRequestDto;
import org.example.dto.property.PropertyResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {
    PropertyResponseDto addProperty(PropertyCreateRequestDto request, List<MultipartFile> images);
    List<PropertyResponseDto> findAll();
    PropertyResponseDto findById(Integer id);
    List<PropertyResponseDto> findAllByUserId(Integer userId);

    default PropertyResponseDto create(PropertyCreateRequestDto request, List<MultipartFile> images) {
        return addProperty(request, images);
    }
}
