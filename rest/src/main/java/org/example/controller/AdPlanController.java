package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.AdPlanDto;
import org.example.service.AdPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ad-plans")
@RequiredArgsConstructor
public class AdPlanController {

    private final AdPlanService adPlanService;

    @GetMapping
    public List<AdPlanDto> getAdPlans() {
        return adPlanService.getAll();
    }

    @GetMapping("/{id}")
    public AdPlanDto getById(@PathVariable int id) {
        return adPlanService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdPlanDto create(@RequestBody AdPlanDto dto) {
        return adPlanService.create(dto);
    }

    @PutMapping
    public AdPlanDto update(@RequestBody AdPlanDto dto) {
        return adPlanService.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        adPlanService.delete(id);
    }
}
