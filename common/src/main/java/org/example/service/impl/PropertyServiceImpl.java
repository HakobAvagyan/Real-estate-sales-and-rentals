package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.property.PropertyCreateRequestDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.mapper.property.PropertyCreateRequestMapper;
import org.example.model.Location;
import org.example.model.LocationName;
import org.example.model.Property;
import org.example.model.PropertyImage;
import org.example.model.User;
import org.example.repository.LocationNameRepository;
import org.example.repository.LocationRepository;
import org.example.repository.PropertyImageRepository;
import org.example.repository.PropertyRepository;
import org.example.repository.UserRepository;
import org.example.service.PropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    @Value("${system.upload.images.directory.path}")
    private String imageDirectoryPath;

    private final PropertyRepository propertyRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final LocationNameRepository locationNameRepository;
    private final PropertyCreateRequestMapper propertyCreateRequestMapper;

    @Override
    @Transactional
    public PropertyResponseDto addProperty(PropertyCreateRequestDto request, List<MultipartFile> images) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getUserId()));
        Location location = resolveLocation(request);

        Property property = propertyCreateRequestMapper.toProperty(request);
        property.setUser(user);
        property.setLocation(location);
        property.setCreatedAt(LocalDate.now());

        Property savedProperty = propertyRepository.save(property);

        List<String> imageUrls = savePropertyImages(savedProperty, normalizeImageList(images));

        return toResponse(savedProperty, imageUrls);
    }

    @Override
    public List<PropertyResponseDto> findAll() {
        return propertyRepository.findAll().stream().map(property -> {
            List<String> imageUrls = propertyImageRepository.findAllByPropertyId(property.getId())
                    .stream()
                    .map(PropertyImage::getImagesUrl)
                    .toList();
            return toResponse(property, imageUrls);
        }).toList();
    }

    @Override
    public Optional<PropertyResponseDto> findById(int id) {
        return propertyRepository.findById(id).map(property -> {
            List<String> imageUrls = propertyImageRepository.findAllByPropertyId(property.getId())
                    .stream()
                    .map(PropertyImage::getImagesUrl)
                    .toList();
            return toResponse(property, imageUrls);
        });
    }

    @Override
    public List<PropertyResponseDto> findAllByUserId(Integer userId) {
        return propertyRepository.findAllByUserId(userId).stream().map(property -> {
            List<String> imageUrls = propertyImageRepository.findAllByPropertyId(property.getId())
                    .stream()
                    .map(PropertyImage::getImagesUrl)
                    .toList();
            return toResponse(property, imageUrls);
        }).toList();
    }

    private List<String> savePropertyImages(Property property, List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();
        if (images == null || images.isEmpty()) {
            log.error("No images uploaded for property {}", property.getId());
            return imageUrls;
        }

        Path uploadDir = Paths.get(imageDirectoryPath, "properties");
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("Unable to create directory for upload images for property {}", property.getId());
        }

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }
            String storedFileName = "property_" + property.getId() + "_" + UUID.randomUUID() + extension;
            Path imagePath = uploadDir.resolve(storedFileName);
            try {
                image.transferTo(imagePath.toFile());
            } catch (IOException e) {
                log.error("Unable to save image for property {}", property.getId());
            }

            String relativePath = "properties/" + storedFileName;

            PropertyImage propertyImage = new PropertyImage();
            propertyImage.setProperty(property);
            propertyImage.setImagesUrl(relativePath);
            propertyImage.setCreatedAt(LocalDate.now());
            propertyImageRepository.save(propertyImage);

            imageUrls.add(relativePath);
        }

        return imageUrls;
    }

    private List<MultipartFile> normalizeImageList(List<MultipartFile> images) {
        if (images == null) {
            log.error("Image list is null, normalizing to empty list");
            return List.of();
        }
        return images;
    }

    private Location resolveLocation(PropertyCreateRequestDto request) {
        if (request.getLocationId() > 0) {
            return locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NOT_FOUND, request.getLocationId()));
        }
        Integer locationNameId = request.getLocationNameId();
        String district = request.getDistrict();
        String street = request.getStreet();
        if (locationNameId == null || district == null || district.isBlank() || street == null || street.isBlank()) {
            log.error("LocationName or district or street is null or blank");
            throw new BusinessException(ErrorCode.INVALID_REQUEST_BODY);
        }
        LocationName locationName = locationNameRepository.findById(locationNameId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LOCATION_NAME_NOT_FOUND, locationNameId));
        return locationRepository.findByLocationNameIdAndDistrictAndStreet(locationNameId, district, street)
                .orElseGet(() -> {
                    Location location = new Location();
                    location.setLocationName(locationName);
                    location.setDistrict(district.trim());
                    location.setStreet(street.trim());
                    return locationRepository.save(location);
                });
    }

    private PropertyResponseDto toResponse(Property property, List<String> imageUrls) {
        return PropertyResponseDto.builder()
                .id(property.getId())
                .userId(property.getUser().getId())
                .locationId(property.getLocation().getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .surface(property.getSurface())
                .roomsCount(property.getRoomsCount())
                .bathroomsCount(property.getBathroomsCount())
                .floorCount(property.getFloorCount())
                .floor(property.getFloor())
                .price(property.getPrice())
                .createdAt(property.getCreatedAt())
                .status(property.getStatus())
                .propertyType(property.getPropertyType())
                .imageUrls(imageUrls)
                .build();
    }
}
