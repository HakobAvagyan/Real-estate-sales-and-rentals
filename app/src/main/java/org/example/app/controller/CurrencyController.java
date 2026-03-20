package org.example.app.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.CurrancyDto;
import org.example.mapper.CurrancyMapper;
import org.example.service.CurrencyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping("/currencies")
    public String getCurrencies(ModelMap modelMap) {
        modelMap.addAttribute("currencies", currencyService.findAll().stream().map(CurrancyMapper::toDto).toList());
        return "currencies";
    }

    @PostMapping("/currencies/update")
    public String updateCurrency(CurrancyDto currancyDto) {
        currencyService.update(CurrancyMapper.toEntity(currancyDto));
        return "redirect:/currencies";
    }

    @GetMapping("/currencies/edit")
    public String editForm(@RequestParam int currencyId, ModelMap modelMap) {
        currencyService.findById(currencyId).ifPresent(currency -> modelMap.addAttribute("currency", CurrancyMapper.toDto(currency)));
        return "currency-edit";
    }
}