package org.example.service;

import org.example.model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    List<Currency> findAll();

    Optional<Currency> findById(int id);

    Optional<Currency> findByCode(String code);

    Currency save(Currency currency);
    Currency update(Currency currency);

    void deleteById(int id);
}
