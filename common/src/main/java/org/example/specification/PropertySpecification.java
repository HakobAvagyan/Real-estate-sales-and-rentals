package org.example.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.example.dto.property.PropertyFilterDto;
import org.example.model.Location;
import org.example.model.LocationName;
import org.example.model.Property;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.example.model.enums.Region;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PropertySpecification {

    public static Specification<Property> withFilter(PropertyFilterDto filter) {
        Specification<Property> spec = (root, q, cb) -> null;
        spec = spec.and(hasType(filter.getPropertyType()));
        spec = spec.and(hasStatus(filter.getStatus()));
        spec = spec.and(priceGte(filter.getMinPrice()));
        spec = spec.and(priceLte(filter.getMaxPrice()));
        spec = spec.and(surfaceGte(filter.getMinSurface()));
        spec = spec.and(surfaceLte(filter.getMaxSurface()));
        spec = spec.and(roomsGte(filter.getMinRooms()));
        spec = spec.and(roomsLte(filter.getMaxRooms()));
        spec = spec.and(bathroomsGte(filter.getMinBathrooms()));
        spec = spec.and(floorGte(filter.getMinFloor()));
        spec = spec.and(floorLte(filter.getMaxFloor()));
        spec = spec.and(hasLocation(filter.getRegion(), filter.getCity()));
        return spec;
    }

    private static final Specification<Property> NOOP = (root, q, cb) -> null;

    private static Specification<Property> hasType(PropertyType type) {
        return type == null ? NOOP : (root, q, cb) -> cb.equal(root.get("propertyType"), type);
    }

    private static Specification<Property> hasStatus(PropertyStatus status) {
        return status == null ? NOOP : (root, q, cb) -> cb.equal(root.get("status"), status);
    }

    private static Specification<Property> priceGte(BigDecimal min) {
        return min == null ? NOOP : (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
    }

    private static Specification<Property> priceLte(BigDecimal max) {
        return max == null ? NOOP : (root, q, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
    }

    private static Specification<Property> surfaceGte(Integer min) {
        return min == null ? NOOP : (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("surface"), min);
    }

    private static Specification<Property> surfaceLte(Integer max) {
        return max == null ? NOOP : (root, q, cb) -> cb.lessThanOrEqualTo(root.get("surface"), max);
    }

    private static Specification<Property> roomsGte(Integer min) {
        return min == null ? NOOP : (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("roomsCount"), min);
    }

    private static Specification<Property> roomsLte(Integer max) {
        return max == null ? NOOP : (root, q, cb) -> cb.lessThanOrEqualTo(root.get("roomsCount"), max);
    }

    private static Specification<Property> bathroomsGte(Integer min) {
        return min == null ? NOOP : (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("bathroomsCount"), min);
    }

    private static Specification<Property> floorGte(Integer min) {
        return min == null ? NOOP : (root, q, cb) -> cb.greaterThanOrEqualTo(root.get("floor"), min);
    }

    private static Specification<Property> floorLte(Integer max) {
        return max == null ? NOOP : (root, q, cb) -> cb.lessThanOrEqualTo(root.get("floor"), max);
    }

    private static Specification<Property> hasLocation(Region region, String city) {
        boolean hasRegion = region != null;
        boolean hasCity = city != null && !city.isBlank();
        if (!hasRegion && !hasCity) return NOOP;
        return (root, q, cb) -> {
            Join<Property, Location> location = root.join("location");
            Join<Location, LocationName> locationName = location.join("locationName");
            List<Predicate> predicates = new ArrayList<>();
            if (hasRegion) predicates.add(cb.equal(locationName.get("region"), region));
            if (hasCity) predicates.add(cb.like(cb.lower(locationName.get("city")), "%" + city.toLowerCase().trim() + "%"));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

