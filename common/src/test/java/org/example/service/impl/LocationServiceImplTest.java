package org.example.service.impl;

import org.example.dto.location.LocationDto;
import org.example.dto.location.LocationNameDto;
import org.example.exception.BusinessException;
import org.example.mapper.location.LocationMapper;
import org.example.model.Location;
import org.example.model.LocationName;
import org.example.model.enums.Region;
import org.example.repository.LocationNameRepository;
import org.example.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplTest {

    @Mock LocationRepository locationRepository;
    @Mock LocationNameRepository locationNameRepository;
    @Mock LocationMapper locationMapper;

    @InjectMocks
    LocationServiceImpl locationService;

    private LocationName locationName(int id, Region region, String city) {
        LocationName ln = new LocationName();
        ln.setId(id);
        ln.setRegion(region);
        ln.setCity(city);
        return ln;
    }

    private Location location(int id, LocationName ln) {
        Location loc = new Location();
        loc.setId(id);
        loc.setLocationName(ln);
        loc.setDistrict("District A");
        loc.setStreet("Street B");
        return loc;
    }

    private LocationDto locationDto(int id, Region region, String city) {
        LocationDto dto = new LocationDto();
        dto.setId(id);
        dto.setRegion(region);
        dto.setCity(city);
        dto.setDistrict("District A");
        dto.setStreet("Street B");
        return dto;
    }

    // --- findById ---

    @Test
    void findById_returnsEmptyWhenNotFound() {
        when(locationRepository.findById(999)).thenReturn(Optional.empty());

        Optional<LocationDto> result = locationService.findById(999);

        assertFalse(result.isPresent());
    }

    @Test
    void findById_returnsMappedDto() {
        LocationName ln = locationName(1, Region.ԵՐԵՎԱՆ, "Yerevan");
        Location loc = location(5, ln);
        LocationDto dto = locationDto(5, Region.ԵՐԵՎԱՆ, "Yerevan");

        when(locationRepository.findById(5)).thenReturn(Optional.of(loc));
        when(locationMapper.toLocationDto(loc)).thenReturn(dto);

        Optional<LocationDto> result = locationService.findById(5);

        assertTrue(result.isPresent());
        assertEquals(5, result.get().getId());
        assertEquals(Region.ԵՐԵՎԱՆ, result.get().getRegion());
    }

    // --- getLocationMap ---

    @Test
    void getLocationMap_returnsMapKeyedById() {
        LocationName ln = locationName(1, Region.ԿՈՏԱՅՔ, "Abovyan");
        Location loc = location(3, ln);
        LocationDto dto = locationDto(3, Region.ԿՈՏԱՅՔ, "Abovyan");

        when(locationRepository.findAll()).thenReturn(List.of(loc));
        when(locationMapper.toLocationDto(loc)).thenReturn(dto);

        Map<Integer, LocationDto> result = locationService.getLocationMap();

        assertEquals(1, result.size());
        assertTrue(result.containsKey(3));
        assertEquals("Abovyan", result.get(3).getCity());
    }

    @Test
    void getLocationMap_returnsEmptyMapWhenNoLocations() {
        when(locationRepository.findAll()).thenReturn(List.of());

        Map<Integer, LocationDto> result = locationService.getLocationMap();

        assertTrue(result.isEmpty());
    }

    // --- save ---

    @Test
    void save_createsLocationWithExistingLocationName() {
        LocationDto input = locationDto(0, Region.ԵՐԵՎԱՆ, "Yerevan");
        LocationName ln = locationName(1, Region.ԵՐԵՎԱՆ, "Yerevan");
        Location saved = location(10, ln);
        LocationDto expectedDto = locationDto(10, Region.ԵՐԵՎԱՆ, "Yerevan");

        when(locationNameRepository.findByRegionAndCity(Region.ԵՐԵՎԱՆ, "Yerevan"))
                .thenReturn(Optional.of(ln));
        when(locationRepository.save(any(Location.class))).thenReturn(saved);
        when(locationMapper.toLocationDto(saved)).thenReturn(expectedDto);

        LocationDto result = locationService.save(input);

        assertEquals(10, result.getId());
        verify(locationNameRepository, never()).save(any());
        ArgumentCaptor<Location> captor = ArgumentCaptor.forClass(Location.class);
        verify(locationRepository).save(captor.capture());
        assertSame(ln, captor.getValue().getLocationName());
    }

    @Test
    void save_createsNewLocationNameWhenNotFound() {
        LocationDto input = locationDto(0, Region.ԼՈՌԻ, "Vanadzor");
        LocationName newLn = locationName(5, Region.ԼՈՌԻ, "Vanadzor");
        Location saved = location(15, newLn);
        LocationDto expectedDto = locationDto(15, Region.ԼՈՌԻ, "Vanadzor");

        when(locationNameRepository.findByRegionAndCity(Region.ԼՈՌԻ, "Vanadzor"))
                .thenReturn(Optional.empty());
        when(locationNameRepository.save(any(LocationName.class))).thenReturn(newLn);
        when(locationRepository.save(any(Location.class))).thenReturn(saved);
        when(locationMapper.toLocationDto(saved)).thenReturn(expectedDto);

        LocationDto result = locationService.save(input);

        assertEquals(15, result.getId());
        verify(locationNameRepository).save(any(LocationName.class));
    }

    // --- update ---

    @Test
    void update_throwsWhenLocationNotFound() {
        LocationDto input = locationDto(999, Region.ԱՐԱՐԱՏ, "Artashat");
        when(locationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> locationService.update(input));
    }

    @Test
    void update_updatesAndReturnsDto() {
        LocationName ln = locationName(1, Region.ԱՐԱՐԱՏ, "Artashat");
        Location existing = location(7, ln);
        LocationDto input = locationDto(7, Region.ԱՐԱՐԱՏ, "Artashat");
        Location updated = location(7, ln);
        LocationDto expectedDto = locationDto(7, Region.ԱՐԱՐԱՏ, "Artashat");

        when(locationRepository.findById(7)).thenReturn(Optional.of(existing));
        when(locationNameRepository.findByRegionAndCity(Region.ԱՐԱՐԱՏ, "Artashat"))
                .thenReturn(Optional.of(ln));
        when(locationRepository.save(existing)).thenReturn(updated);
        when(locationMapper.toLocationDto(updated)).thenReturn(expectedDto);

        LocationDto result = locationService.update(input);

        assertEquals(7, result.getId());
        verify(locationRepository).save(existing);
    }

    // --- getAll ---

    @Test
    void getAll_returnsMappedList() {
        LocationName ln = locationName(1, Region.ՇԻՐԱԿ, "Gyumri");
        Location loc = location(2, ln);
        LocationDto dto = locationDto(2, Region.ՇԻՐԱԿ, "Gyumri");

        when(locationRepository.findAll()).thenReturn(List.of(loc));
        when(locationMapper.toLocationDto(loc)).thenReturn(dto);

        List<LocationDto> result = locationService.getAll();

        assertEquals(1, result.size());
        assertEquals("Gyumri", result.get(0).getCity());
    }

    // --- getAllLocationNames ---

    @Test
    void getAllLocationNames_returnsMappedList() {
        LocationName ln1 = locationName(1, Region.ԵՐԵՎԱՆ, "Yerevan");
        LocationName ln2 = locationName(2, Region.ԿՈՏԱՅՔ, "Abovyan");

        when(locationNameRepository.findAll()).thenReturn(List.of(ln1, ln2));

        List<LocationNameDto> result = locationService.getAllLocationNames();

        assertEquals(2, result.size());
        assertEquals(Region.ԵՐԵՎԱՆ, result.get(0).getRegion());
        assertEquals("Yerevan", result.get(0).getCity());
        assertEquals(Region.ԿՈՏԱՅՔ, result.get(1).getRegion());
    }

    @Test
    void getAllLocationNames_returnsEmptyList() {
        when(locationNameRepository.findAll()).thenReturn(List.of());

        List<LocationNameDto> result = locationService.getAllLocationNames();

        assertTrue(result.isEmpty());
    }
}
