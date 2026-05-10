package org.example.service.impl;

import org.example.dto.ratings.PropertyRatingSummaryDto;
import org.example.dto.ratings.PropertyRatingViewDto;
import org.example.exception.BusinessException;
import org.example.model.Property;
import org.example.model.Ratings;
import org.example.model.User;
import org.example.repository.PropertyRepository;
import org.example.repository.RatingsRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingsServiceImplTest {

    @Mock RatingsRepository ratingsRepository;
    @Mock PropertyRepository propertyRepository;
    @Mock UserRepository userRepository;

    @InjectMocks
    RatingsServiceImpl ratingsService;

    private User user(int id) {
        User u = new User();
        u.setId(id);
        u.setName("User" + id);
        u.setSurname("Test");
        return u;
    }

    private Property property(int id, int ownerId) {
        Property p = new Property();
        p.setId(id);
        p.setUser(user(ownerId));
        return p;
    }

    private Ratings rating(int id, int stars, String review, User u, Property p) {
        Ratings r = new Ratings();
        r.setId(id);
        r.setRating(stars);
        r.setReviewText(review);
        r.setRatedAt(LocalDateTime.now());
        r.setUser(u);
        r.setProperty(p);
        return r;
    }

    // --- getSummary ---

    @Test
    void getSummary_returnsZeroWhenNoRatings() {
        when(ratingsRepository.aggregateForProperty(1)).thenReturn(List.of());

        PropertyRatingSummaryDto dto = ratingsService.getSummary(1);

        assertEquals(0.0, dto.getAverageStars());
        assertEquals(0L, dto.getReviewCount());
        assertFalse(dto.hasReviews());
    }

    @Test
    void getSummary_returnsCorrectAvgAndCount() {
        when(ratingsRepository.aggregateForProperty(10))
                .thenReturn(java.util.Arrays.asList(new Object[][]{{4.25, 8L}}));

        PropertyRatingSummaryDto dto = ratingsService.getSummary(10);

        assertEquals(4.3, dto.getAverageStars());
        assertEquals(8L, dto.getReviewCount());
        assertTrue(dto.hasReviews());
    }

    @Test
    void getSummary_roundsToOneDecimal() {
        when(ratingsRepository.aggregateForProperty(2))
                .thenReturn(java.util.Arrays.asList(new Object[][]{{3.666, 3L}}));

        PropertyRatingSummaryDto dto = ratingsService.getSummary(2);
        assertEquals(3.7, dto.getAverageStars());
    }

    // --- getSummariesForPropertyIds ---

    @Test
    void getSummariesForPropertyIds_returnsEmptyForNull() {
        Map<Integer, PropertyRatingSummaryDto> result = ratingsService.getSummariesForPropertyIds(null);
        assertTrue(result.isEmpty());
        verifyNoInteractions(ratingsRepository);
    }

    @Test
    void getSummariesForPropertyIds_returnsEmptyForEmptyList() {
        Map<Integer, PropertyRatingSummaryDto> result = ratingsService.getSummariesForPropertyIds(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void getSummariesForPropertyIds_fillsZeroForMissingIds() {
        when(ratingsRepository.aggregateForPropertyIds(anyList()))
                .thenReturn(java.util.Arrays.asList(new Object[][]{{5, 4.0, 2L}}));

        Map<Integer, PropertyRatingSummaryDto> result = ratingsService.getSummariesForPropertyIds(List.of(5, 6));

        assertEquals(4.0, result.get(5).getAverageStars());
        assertEquals(2L, result.get(5).getReviewCount());
        assertEquals(0.0, result.get(6).getAverageStars());
        assertEquals(0L, result.get(6).getReviewCount());
    }

    // --- listReviewsForProperty ---

    @Test
    void listReviewsForProperty_mapsCorrectly() {
        User u = user(1);
        Property p = property(10, 99);
        Ratings r = rating(1, 4, "Very nice", u, p);
        when(ratingsRepository.findByProperty_IdOrderByRatedAtDesc(10)).thenReturn(List.of(r));

        List<PropertyRatingViewDto> views = ratingsService.listReviewsForProperty(10);

        assertEquals(1, views.size());
        PropertyRatingViewDto view = views.get(0);
        assertEquals(1, view.getId());
        assertEquals(4, view.getStars());
        assertEquals("Very nice", view.getReviewText());
        assertEquals("User1 Test", view.getAuthorDisplayName());
        assertEquals(1, view.getUserId());
    }

    @Test
    void listReviewsForProperty_returnsEmptyList() {
        when(ratingsRepository.findByProperty_IdOrderByRatedAtDesc(1)).thenReturn(List.of());
        assertTrue(ratingsService.listReviewsForProperty(1).isEmpty());
    }

    // --- findReviewByUser ---

    @Test
    void findReviewByUser_returnsEmptyWhenNone() {
        when(ratingsRepository.findByProperty_IdAndUser_Id(1, 2)).thenReturn(Optional.empty());
        assertTrue(ratingsService.findReviewByUser(1, 2).isEmpty());
    }

    @Test
    void findReviewByUser_returnsMappedDto() {
        User u = user(2);
        Property p = property(1, 99);
        Ratings r = rating(5, 3, "OK", u, p);
        when(ratingsRepository.findByProperty_IdAndUser_Id(1, 2)).thenReturn(Optional.of(r));

        Optional<PropertyRatingViewDto> result = ratingsService.findReviewByUser(1, 2);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().getStars());
    }

    // --- upsertReview ---

    @Test
    void upsertReview_throwsWhenStarsLessThan1() {
        assertThrows(BusinessException.class,
                () -> ratingsService.upsertReview(1, 10, 0, "text"));
        verifyNoInteractions(propertyRepository, userRepository, ratingsRepository);
    }

    @Test
    void upsertReview_throwsWhenStarsGreaterThan5() {
        assertThrows(BusinessException.class,
                () -> ratingsService.upsertReview(1, 10, 6, "text"));
    }

    @Test
    void upsertReview_throwsWhenUserRatesOwnListing() {
        Property p = property(10, 1);
        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));

        assertThrows(BusinessException.class,
                () -> ratingsService.upsertReview(1, 10, 4, "nice"));
    }

    @Test
    void upsertReview_throwsWhenPropertyNotFound() {
        when(propertyRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> ratingsService.upsertReview(1, 999, 3, "text"));
    }

    @Test
    void upsertReview_throwsWhenReviewTooLong() {
        Property p = property(10, 99);
        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));
        when(userRepository.findById(1)).thenReturn(Optional.of(user(1)));

        String longReview = "x".repeat(2001);
        assertThrows(BusinessException.class,
                () -> ratingsService.upsertReview(1, 10, 4, longReview));
    }

    @Test
    void upsertReview_createsNewRatingWhenNoneExists() {
        Property p = property(10, 99);
        User u = user(1);
        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(ratingsRepository.findByProperty_IdAndUser_Id(10, 1)).thenReturn(Optional.empty());

        ratingsService.upsertReview(1, 10, 5, "Excellent!");

        ArgumentCaptor<Ratings> captor = ArgumentCaptor.forClass(Ratings.class);
        verify(ratingsRepository).save(captor.capture());
        Ratings saved = captor.getValue();
        assertEquals(5, saved.getRating());
        assertEquals("Excellent!", saved.getReviewText());
        assertSame(p, saved.getProperty());
        assertSame(u, saved.getUser());
    }

    @Test
    void upsertReview_updatesExistingRating() {
        Property p = property(10, 99);
        User u = user(1);
        Ratings existing = rating(5, 2, "Old text", u, p);

        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(ratingsRepository.findByProperty_IdAndUser_Id(10, 1)).thenReturn(Optional.of(existing));

        ratingsService.upsertReview(1, 10, 4, "Updated review");

        verify(ratingsRepository).save(existing);
        assertEquals(4, existing.getRating());
        assertEquals("Updated review", existing.getReviewText());
    }

    @Test
    void upsertReview_acceptsNullReviewText() {
        Property p = property(10, 99);
        User u = user(1);
        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(ratingsRepository.findByProperty_IdAndUser_Id(10, 1)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> ratingsService.upsertReview(1, 10, 3, null));

        ArgumentCaptor<Ratings> captor = ArgumentCaptor.forClass(Ratings.class);
        verify(ratingsRepository).save(captor.capture());
        assertNull(captor.getValue().getReviewText());
    }

    @Test
    void upsertReview_acceptsExactlyMaxLengthReview() {
        Property p = property(10, 99);
        User u = user(1);
        when(propertyRepository.findById(10)).thenReturn(Optional.of(p));
        when(userRepository.findById(1)).thenReturn(Optional.of(u));
        when(ratingsRepository.findByProperty_IdAndUser_Id(10, 1)).thenReturn(Optional.empty());

        String maxReview = "x".repeat(2000);
        assertDoesNotThrow(() -> ratingsService.upsertReview(1, 10, 4, maxReview));
    }
}
