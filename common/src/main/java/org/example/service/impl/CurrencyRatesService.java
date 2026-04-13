package org.example.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CurrencyRatesService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";

    public double convert(double amountInUsd, String toCurrency) {
        double rate = getRate(toCurrency);
        return amountInUsd * rate;
    }

    public double getRate(String currencyCode) {
        Map<?, ?> response = restTemplate.getForObject(API_URL, Map.class);
        if (response == null) return 1.0;
        Object rates = response.get("rates");
        if (!(rates instanceof Map)) return 1.0;
        Object value = ((Map<?, ?>) rates).get(currencyCode);
        return value instanceof Number ? ((Number) value).doubleValue() : 1.0;
    }
}
