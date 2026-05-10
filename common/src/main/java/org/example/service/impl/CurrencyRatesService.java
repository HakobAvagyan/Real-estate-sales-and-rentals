package org.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyRatesService {

    private static final List<String> SUPPORTED = List.of("USD", "EUR", "RUB", "AMD");
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";
    private static final long CACHE_TTL_SECONDS = 3600;

    private final RestTemplate restTemplate = new RestTemplate();
    private Map<String, Double> cachedRates = new HashMap<>();
    private Instant lastFetched = Instant.EPOCH;

    public List<String> getSupportedCurrencies() {
        return SUPPORTED;
    }

    public double convert(double amountInUsd, String toCurrency) {
        return amountInUsd * getRate(toCurrency);
    }

    public BigDecimal convertPrice(BigDecimal price, String toCurrency) {
        if ("USD".equals(toCurrency)) {
            return price;
        }
        double rate = getRate(toCurrency);
        return price.multiply(BigDecimal.valueOf(rate)).setScale(0, RoundingMode.HALF_UP);
    }

    public double getRate(String currencyCode) {
        refreshIfStale();
        return cachedRates.getOrDefault(currencyCode, 1.0);
    }

    private synchronized void refreshIfStale() {
        if (Instant.now().minusSeconds(CACHE_TTL_SECONDS).isBefore(lastFetched)) {
            return;
        }
        try {
            Map<?, ?> response = restTemplate.getForObject(API_URL, Map.class);
            if (response != null && response.get("rates") instanceof Map<?, ?> rates) {
                Map<String, Double> fresh = new HashMap<>();
                for (String code : SUPPORTED) {
                    Object v = rates.get(code);
                    fresh.put(code, v instanceof Number n ? n.doubleValue() : 1.0);
                }
                cachedRates = fresh;
                lastFetched = Instant.now();
            }
        } catch (Exception ignored) {
            // keep stale cache on failure
        }
    }
}