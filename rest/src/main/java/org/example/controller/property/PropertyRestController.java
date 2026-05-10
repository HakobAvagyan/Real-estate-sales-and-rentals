package org.example.controller.property;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.dto.property.PropertyCreateRequestDto;
import org.example.dto.property.PropertyResponseDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.service.PropertyService;
import org.example.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.example.dto.property.PropertyDetailsResponse;
import org.example.dto.property.PropertyListResponse;
import org.example.dto.property.PropertyFilterDto;
import org.example.dto.ratings.PropertyRatingSummaryDto;
import org.example.model.enums.Role;
import org.example.service.CommentService;
import org.example.service.RatingsService;
import org.example.service.impl.CurrencyRatesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyRestController {

    private final PropertyService propertyService;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final CurrencyRatesService currencyRatesService;
    private final RatingsService ratingsService;
    private final CommentService commentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PropertyResponseDto createProperty(
            @RequestPart("property") String propertyJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageList
    ) {
        PropertyCreateRequestDto request;
        try {
            request = objectMapper.readValue(propertyJson, PropertyCreateRequestDto.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST_BODY);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new BusinessException(ErrorCode.USER_NOT_AUTHENTICATED);
        }
        request.setUserId(userService.findByEmail(authentication.getName()).getId());
        return propertyService.addProperty(request, imageList);
    }

    @GetMapping
    public PropertyListResponse getProperties(
            @ModelAttribute PropertyFilterDto filter,
            @RequestParam(required = false, defaultValue = "USD") String currency) {
        
        List<PropertyResponseDto> properties = propertyService.findAllFiltered(filter);
        List<Integer> propertyIds = properties.stream().map(PropertyResponseDto::getId).toList();
        
        Map<Integer, PropertyRatingSummaryDto> ratingSummaries = ratingsService.getSummariesForPropertyIds(propertyIds);
        Map<Integer, Long> commentCounts = commentService.getCommentCountsForPropertyIds(propertyIds);
        
        Map<Integer, BigDecimal> convertedPrices = new LinkedHashMap<>();
        if (!"USD".equals(currency)) {
            for (PropertyResponseDto p : properties) {
                convertedPrices.put(p.getId(), currencyRatesService.convertPrice(p.getPrice(), currency));
            }
        }
        
        return new PropertyListResponse(properties, ratingSummaries, commentCounts, convertedPrices, currency);
    }

    @GetMapping("/{id}")
    public PropertyDetailsResponse getPropertyDetails(
            @PathVariable("id") int propertyId,
            @RequestParam(required = false, defaultValue = "USD") String currency) {
            
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer viewerId = null;
        Role viewerRole = null;
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            org.example.dto.user.UserRegisterDto user = userService.findByEmail(authentication.getName());
            if (user != null) {
                viewerId = user.getId();
                viewerRole = user.getRole();
            }
        }
        
        PropertyResponseDto property = propertyService.findByIdForDisplay(propertyId, viewerId, viewerRole)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));
                
        List<PropertyResponseDto> similarProperties = propertyService.findSimilarProperties(property, 6);
        BigDecimal convertedPrice = null;
        if (!"USD".equals(currency)) {
            convertedPrice = currencyRatesService.convertPrice(property.getPrice(), currency);
        }
        
        return new PropertyDetailsResponse(property, similarProperties, convertedPrice, currency);
    }

    @GetMapping("/{id}/similar")
    public List<PropertyResponseDto> getSimilarProperties(@PathVariable("id") int propertyId) {
        PropertyResponseDto property = propertyService.findById(propertyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));
        return propertyService.findSimilarProperties(property, 6);
    }
}
