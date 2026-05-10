package org.example.dto.admin;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PropertyMarketStatsDto {
    long pendingModeration;
    long totalListings;
    long forSale;
    long forRent;
    long sold;
    long rented;
    long typeHouse;
    long typeApartment;
    long typeLand;
}
