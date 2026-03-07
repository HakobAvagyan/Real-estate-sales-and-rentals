package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Ratings;
import org.example.repository.RatingsRepository;
import org.example.service.RatingsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingsServiceImpl implements RatingsService {

    private  final RatingsRepository ratingsRepository;

    @Override
    public Optional<Ratings> findById(Integer id) {
        return ratingsRepository.findById(id);
    }

    @Override
    public Optional<Ratings> findByUserId(Integer userId) {
        return ratingsRepository.findByUserId(userId);
    }

    @Override
    public Optional<Ratings> findByPropertyId(Integer propertyId) {
        return ratingsRepository.findByPropertyId(propertyId);
    }

    @Override
    public void save(Ratings ratings) {
        ratingsRepository.save(ratings);
    }

    @Override
    public void deleteById(Integer id) {
        ratingsRepository.deleteById(id);
    }

    @Override
    public Ratings update(Ratings ratings) {
        return ratingsRepository.save(ratings);
    }
}
