package org.example.api;

import lombok.RequiredArgsConstructor;
import org.example.dto.AdPlanDto;
import org.example.service.AdPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ad-plans")
@RequiredArgsConstructor
public class ApiAdPlanController {

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
