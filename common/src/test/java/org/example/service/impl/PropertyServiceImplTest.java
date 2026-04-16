package org.example.service.impl;

import org.example.dto.property.PropertyCreateRequestDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.mapper.property.PropertyCreateRequestMapper;
import org.example.model.Location;
import org.example.model.Property;
import org.example.model.PropertyImage;
import org.example.model.User;
import org.example.repository.LocationNameRepository;
import org.example.repository.LocationRepository;
import org.example.repository.PropertyImageRepository;
import org.example.repository.PropertyRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private PropertyImageRepository propertyImageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private LocationNameRepository locationNameRepository;
    @Mock
    private PropertyCreateRequestMapper propertyCreateRequestMapper;

    @InjectMocks
    private PropertyServiceImpl propertyService;

    @Test
    void addProperty_shouldCreateProperty_withoutImages() throws Exception {
        User user = new User();
        user.setId(7);

        Location location = new Location();
        location.setId(11);

        PropertyCreateRequestDto request = new PropertyCreateRequestDto();
        request.setUserId(7);
        request.setLocationId(11);
        request.setTitle("Test title");
        request.setDescription("Test description");
        request.setSurface(60);
        request.setFloor(3);
        request.setPrice(BigDecimal.valueOf(120000));

        Property mapped = new Property();
        mapped.setTitle(request.getTitle());
        mapped.setDescription(request.getDescription());
        mapped.setSurface(request.getSurface());
        mapped.setFloor(request.getFloor());
        mapped.setPrice(request.getPrice());

        when(userRepository.findById(7)).thenReturn(Optional.of(user));
        when(locationRepository.findById(11)).thenReturn(Optional.of(location));
        when(propertyCreateRequestMapper.toProperty(request)).thenReturn(mapped);
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property saved = invocation.getArgument(0);
            saved.setId(101);
            return saved;
        });

        Path tempDir = Files.createTempDirectory("property-test");
        setImageDirectory(tempDir.toString());

        PropertyResponseDto response = propertyService.addProperty(request, null);

        assertNotNull(response);
        assertEquals(101, response.getId());
        assertEquals(7, response.getUserId());
        assertEquals(11, response.getLocationId());
        assertNotNull(response.getImageUrls());
        assertTrue(response.getImageUrls().isEmpty());
        verify(propertyRepository).save(any(Property.class));
    }

    @Test
    void findAllByUserId_shouldReturnMappedPropertiesWithImages() {
        User user = new User();
        user.setId(5);

        Location location = new Location();
        location.setId(9);

        Property property = new Property();
        property.setId(44);
        property.setUser(user);
        property.setLocation(location);
        property.setTitle("Flat");
        property.setDescription("Desc");
        property.setSurface(70);
        property.setFloor(6);
        property.setPrice(BigDecimal.TEN);
        property.setCreatedAt(LocalDate.now());

        PropertyImage image = new PropertyImage();
        image.setImagesUrl("properties/pic.jpg");

        when(propertyRepository.findAllByUserId(5)).thenReturn(List.of(property));
        when(propertyImageRepository.findAllByPropertyId(44)).thenReturn(List.of(image));

        List<PropertyResponseDto> result = propertyService.findAllByUserId(5);

        assertEquals(1, result.size());
        PropertyResponseDto dto = result.get(0);
        assertEquals(44, dto.getId());
        assertEquals(5, dto.getUserId());
        assertEquals(9, dto.getLocationId());
        assertFalse(dto.getImageUrls().isEmpty());
        assertEquals("properties/pic.jpg", dto.getImageUrls().get(0));
    }

    private void setImageDirectory(String value) throws Exception {
        Field field = PropertyServiceImpl.class.getDeclaredField("imageDirectoryPath");
        field.setAccessible(true);
        field.set(propertyService, value);
    }
}
