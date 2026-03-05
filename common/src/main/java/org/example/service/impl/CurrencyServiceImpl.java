package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Currency;
import org.example.repository.CurrencyRepository;
import org.example.service.CurrencyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    @Override
    public Optional<Currency> findById(int id) {
        return currencyRepository.findById(id);
    }

    @Override
    public Optional<Currency> findByCode(String code) {
        return currencyRepository.findByCode(code);
    }

    @Override
    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public Currency update(Currency currency) {
        return currencyRepository.save(currency);
    }

    @Override
    public void deleteById(int id) {
        currencyRepository.deleteById(id);
    }
}
