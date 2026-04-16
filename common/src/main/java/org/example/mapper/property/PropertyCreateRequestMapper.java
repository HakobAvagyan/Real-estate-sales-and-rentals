package org.example.mapper.property;

import org.example.dto.property.PropertyCreateRequestDto;
import org.example.model.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PropertyCreateRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Property toProperty(PropertyCreateRequestDto requestDto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "locationId", source = "location.id")
    @Mapping(target = "locationNameId", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "street", ignore = true)
    PropertyCreateRequestDto toRequestDto(Property property);

}
