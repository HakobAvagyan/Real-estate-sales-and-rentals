package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ratings.PropertyRatingSummaryDto;
import org.example.dto.ratings.PropertyRatingViewDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.Property;
import org.example.model.Ratings;
import org.example.model.User;
import org.example.repository.PropertyRepository;
import org.example.repository.RatingsRepository;
import org.example.repository.UserRepository;
import org.example.service.RatingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingsServiceImpl implements RatingsService {

    private static final int MAX_REVIEW_LENGTH = 2000;

    private final RatingsRepository ratingsRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Override
    public PropertyRatingSummaryDto getSummary(int propertyId) {
        List<Object[]> rows = ratingsRepository.aggregateForProperty(propertyId);
        if (rows.isEmpty()) {
            return new PropertyRatingSummaryDto(0.0, 0L);
        }
        Object[] row = rows.get(0);
        double avg = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
        long count = row[1] != null ? ((Number) row[1]).longValue() : 0L;
        return new PropertyRatingSummaryDto(roundOneDecimal(avg), count);
    }

    @Override
    public Map<Integer, PropertyRatingSummaryDto> getSummariesForPropertyIds(Collection<Integer> propertyIds) {
        if (propertyIds == null || propertyIds.isEmpty()) {
            return Map.of();
        }
        List<Integer> ids = propertyIds.stream().distinct().toList();
        List<Object[]> rows = ratingsRepository.aggregateForPropertyIds(ids);
        Map<Integer, PropertyRatingSummaryDto> map = new HashMap<>();
        for (Object[] row : rows) {
            int pid = ((Number) row[0]).intValue();
            double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            long count = row[2] != null ? ((Number) row[2]).longValue() : 0L;
            map.put(pid, new PropertyRatingSummaryDto(roundOneDecimal(avg), count));
        }
        for (Integer id : ids) {
            map.putIfAbsent(id, new PropertyRatingSummaryDto(0.0, 0L));
        }
        return map;
    }

    @Override
    public List<PropertyRatingViewDto> listReviewsForProperty(int propertyId) {
        return ratingsRepository.findByProperty_IdOrderByRatedAtDesc(propertyId).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PropertyRatingViewDto> findReviewByUser(int propertyId, int userId) {
        return ratingsRepository.findByProperty_IdAndUser_Id(propertyId, userId).map(this::toView);
    }

    @Override
    @Transactional
    public void upsertReview(int userId, int propertyId, int stars, String reviewText) {
        if (stars < 1 || stars > 5) {
            throw new BusinessException(ErrorCode.RATING_STARS_OUT_OF_RANGE);
        }
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));
        if (property.getUser().getId() == userId) {
            throw new BusinessException(ErrorCode.RATING_CANNOT_RATE_OWN_LISTING);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        String text = StringUtils.hasText(reviewText) ? reviewText.trim() : null;
        if (text != null && text.length() > MAX_REVIEW_LENGTH) {
            throw new BusinessException(ErrorCode.RATING_REVIEW_TEXT_TOO_LONG);
        }

        LocalDateTime now = LocalDateTime.now();
        Optional<Ratings> existing = ratingsRepository.findByProperty_IdAndUser_Id(propertyId, userId);
        if (existing.isPresent()) {
            Ratings r = existing.get();
            r.setRating(stars);
            r.setReviewText(text);
            r.setRatedAt(now);
            ratingsRepository.save(r);
        } else {
            Ratings r = new Ratings();
            r.setRating(stars);
            r.setReviewText(text);
            r.setRatedAt(now);
            r.setProperty(property);
            r.setUser(user);
            ratingsRepository.save(r);
        }
    }

    private PropertyRatingViewDto toView(Ratings r) {
        User u = r.getUser();
        String author = authorLabel(u);
        return new PropertyRatingViewDto(
                r.getId(),
                r.getRating(),
                r.getReviewText(),
                r.getRatedAt(),
                u.getId(),
                author
        );
    }

    private static String authorLabel(User u) {
        String first = u.getName() != null ? u.getName().trim() : "";
        String last = u.getSurname() != null ? u.getSurname().trim() : "";
        String combined = (first + " " + last).trim();
        if (!combined.isEmpty()) {
            return combined;
        }
        return u.getEmail() != null ? u.getEmail() : ("User #" + u.getId());
    }

    private static double roundOneDecimal(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
