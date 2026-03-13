package org.example.mapper;

import org.example.dto.CurrancyDto;
import org.example.model.Currency;

public class CurrancyMapper {
    public static CurrancyDto toDto(Currency currency) {
        if (currency == null) {
            return null;
        }
        CurrancyDto currancyDto = new CurrancyDto();
        currancyDto.setId(currency.getId());
        currancyDto.setCode(currency.getCode());
        currancyDto.setName(currency.getName());
        currancyDto.setSymbol(currency.getSymbol());
        currancyDto.setExchangeRate(currency.getExchangeRate());
        return currancyDto;
    }
    public static Currency toEntity(CurrancyDto currancyDto) {
        if (currancyDto == null) {
            return null;
        }
        Currency currency = new Currency();
        currency.setId(currancyDto.getId());
        currency.setCode(currancyDto.getCode());
        currency.setName(currancyDto.getName());
        currency.setSymbol(currancyDto.getSymbol());
        currency.setExchangeRate(currancyDto.getExchangeRate());
        return currency;
    }
}
