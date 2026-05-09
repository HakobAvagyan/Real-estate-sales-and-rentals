package org.example.service;

import org.example.dto.property.Property360Dto;

import java.util.Optional;

public interface Property360Service {
    Property360Dto addOrUpdate(int propertyId, String viewUrl, int currentUserId);
    Optional<Property360Dto> getByPropertyId(int propertyId);
    void delete(int propertyId, int currentUserId);
}