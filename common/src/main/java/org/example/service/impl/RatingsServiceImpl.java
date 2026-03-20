package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ratings.RatingsDto;
import org.example.mapper.ratings.RatingsMapper;
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
    public Optional<RatingsDto> findById(Integer id) {
        return ratingsRepository.findById(id).map(RatingsMapper::toRatingsDto);
    }

    @Override
    public Optional<RatingsDto> findByUserId(Integer userId) {
        return ratingsRepository.findByUserId(userId).map(RatingsMapper::toRatingsDto);
    }

    @Override
    public Optional<RatingsDto> findByPropertyId(Integer propertyId) {
        return ratingsRepository.findByPropertyId(propertyId).map(RatingsMapper::toRatingsDto);
    }

    @Override
    public RatingsDto save(RatingsDto ratings) {
        Ratings ratingsEntity = RatingsMapper.toRatings(ratings);
        ratingsRepository.save(ratingsEntity);
        return RatingsMapper.toRatingsDto(ratingsEntity);
    }

    @Override
    public void deleteById(Integer id) {
        ratingsRepository.deleteById(id);
    }

    @Override
    public RatingsDto update(RatingsDto ratings) {
        Ratings ratingsEntity = RatingsMapper.toRatings(ratings);
        ratingsRepository.save(ratingsEntity);
        return RatingsMapper.toRatingsDto(ratingsEntity);
    }
}
