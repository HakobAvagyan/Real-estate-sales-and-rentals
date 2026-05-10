package org.example.service.impl;

import org.example.dto.admin.AdminPlatformOverviewDto;
import org.example.dto.admin.ManagerWorkloadDto;
import org.example.dto.admin.PropertyMarketStatsDto;
import org.example.model.User;
import org.example.model.enums.PropertyModerationStatus;
import org.example.model.enums.PropertyStatus;
import org.example.model.enums.PropertyType;
import org.example.model.enums.Role;
import org.example.repository.BookingRepository;
import org.example.repository.CommentRepository;
import org.example.repository.MessageChatRepository;
import org.example.repository.PropertyRepository;
import org.example.repository.RatingsRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {

    @Mock PropertyRepository propertyRepository;
    @Mock UserRepository userRepository;
    @Mock MessageChatRepository messageChatRepository;
    @Mock CommentRepository commentRepository;
    @Mock RatingsRepository ratingsRepository;
    @Mock BookingRepository bookingRepository;

    @InjectMocks
    AdminDashboardServiceImpl adminDashboardService;

    // --- getPropertyMarketStats ---

    @Test
    void getPropertyMarketStats_returnsCorrectStats() {
        when(propertyRepository.count()).thenReturn(100L);
        when(propertyRepository.countByModerationStatus(PropertyModerationStatus.PENDING)).thenReturn(5L);

        Object[] statusRow1 = {PropertyStatus.FOR_SALE, 40L};
        Object[] statusRow2 = {PropertyStatus.FOR_RENT, 30L};
        Object[] statusRow3 = {PropertyStatus.SOLD, 20L};
        Object[] statusRow4 = {PropertyStatus.RENTED, 10L};
        when(propertyRepository.countGroupedByStatus())
                .thenReturn(List.of(statusRow1, statusRow2, statusRow3, statusRow4));

        Object[] typeRow1 = {PropertyType.HOUSE, 50L};
        Object[] typeRow2 = {PropertyType.APARTMENT, 35L};
        Object[] typeRow3 = {PropertyType.LAND, 15L};
        when(propertyRepository.countGroupedByPropertyType())
                .thenReturn(List.of(typeRow1, typeRow2, typeRow3));

        PropertyMarketStatsDto stats = adminDashboardService.getPropertyMarketStats();

        assertEquals(100L, stats.getTotalListings());
        assertEquals(5L, stats.getPendingModeration());
        assertEquals(40L, stats.getForSale());
        assertEquals(30L, stats.getForRent());
        assertEquals(20L, stats.getSold());
        assertEquals(10L, stats.getRented());
        assertEquals(50L, stats.getTypeHouse());
        assertEquals(35L, stats.getTypeApartment());
        assertEquals(15L, stats.getTypeLand());
    }

    @Test
    void getPropertyMarketStats_returnsZerosForMissingStatusOrType() {
        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.countByModerationStatus(any())).thenReturn(0L);
        when(propertyRepository.countGroupedByStatus()).thenReturn(List.of());
        when(propertyRepository.countGroupedByPropertyType()).thenReturn(List.of());

        PropertyMarketStatsDto stats = adminDashboardService.getPropertyMarketStats();

        assertEquals(0L, stats.getTotalListings());
        assertEquals(0L, stats.getForSale());
        assertEquals(0L, stats.getForRent());
        assertEquals(0L, stats.getTypeHouse());
    }

    // --- getPlatformOverview ---

    @Test
    void getPlatformOverview_returnsCorrectCounts() {
        when(userRepository.count()).thenReturn(200L);
        when(userRepository.countByRole(Role.USER)).thenReturn(150L);
        when(userRepository.countByRole(Role.MANAGER)).thenReturn(45L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(5L);
        when(userRepository.countByIsBlockedTrue()).thenReturn(10L);
        when(commentRepository.count()).thenReturn(500L);
        when(ratingsRepository.count()).thenReturn(300L);
        when(bookingRepository.count()).thenReturn(120L);

        AdminPlatformOverviewDto overview = adminDashboardService.getPlatformOverview();

        assertEquals(200L, overview.getTotalUsers());
        assertEquals(150L, overview.getUsersRoleUser());
        assertEquals(45L, overview.getUsersRoleManager());
        assertEquals(5L, overview.getUsersRoleAdmin());
        assertEquals(10L, overview.getBlockedUsers());
        assertEquals(500L, overview.getTotalComments());
        assertEquals(300L, overview.getTotalRatings());
        assertEquals(120L, overview.getTotalBookings());
    }

    // --- getManagerWorkloadsSorted ---

    @Test
    void getManagerWorkloadsSorted_returnsManagersWithStats() {
        User manager = new User();
        manager.setId(1);
        manager.setName("Alice");
        manager.setSurname("Brown");
        manager.setEmail("alice@example.com");

        when(userRepository.findUserByRole(Role.MANAGER)).thenReturn(List.of(manager));

        Object[] row = {1, 20L, 5L, LocalDateTime.of(2025, 1, 10, 12, 0)};
        List<Object[]> rows = new java.util.ArrayList<>();
        rows.add(row);
        when(messageChatRepository.aggregateStatsForManagers(Role.MANAGER)).thenReturn(rows);

        List<ManagerWorkloadDto> result = adminDashboardService.getManagerWorkloadsSorted();

        assertEquals(1, result.size());
        ManagerWorkloadDto dto = result.get(0);
        assertEquals(1, dto.getManagerId());
        assertEquals("Alice Brown", dto.getDisplayName());
        assertEquals("alice@example.com", dto.getEmail());
        assertEquals(20L, dto.getMessagesSent());
        assertEquals(5L, dto.getChatsHandled());
        assertNotNull(dto.getLastMessageAt());
    }

    @Test
    void getManagerWorkloadsSorted_zeroStatsWhenNoMessages() {
        User manager = new User();
        manager.setId(2);
        manager.setName("Bob");
        manager.setSurname("Smith");
        manager.setEmail("bob@example.com");

        when(userRepository.findUserByRole(Role.MANAGER)).thenReturn(List.of(manager));
        when(messageChatRepository.aggregateStatsForManagers(Role.MANAGER)).thenReturn(List.of());

        List<ManagerWorkloadDto> result = adminDashboardService.getManagerWorkloadsSorted();

        assertEquals(1, result.size());
        assertEquals(0L, result.get(0).getMessagesSent());
        assertEquals(0L, result.get(0).getChatsHandled());
        assertNull(result.get(0).getLastMessageAt());
    }

    @Test
    void getManagerWorkloadsSorted_sortsByMessagesSentDesc() {
        User m1 = new User(); m1.setId(1); m1.setName("Low"); m1.setSurname("M"); m1.setEmail("low@x.com");
        User m2 = new User(); m2.setId(2); m2.setName("High"); m2.setSurname("M"); m2.setEmail("high@x.com");

        when(userRepository.findUserByRole(Role.MANAGER)).thenReturn(List.of(m1, m2));

        Object[] row1 = {1, 5L, 2L, null};
        Object[] row2 = {2, 50L, 10L, null};
        List<Object[]> sortRows = new java.util.ArrayList<>();
        sortRows.add(row1);
        sortRows.add(row2);
        when(messageChatRepository.aggregateStatsForManagers(Role.MANAGER)).thenReturn(sortRows);

        List<ManagerWorkloadDto> result = adminDashboardService.getManagerWorkloadsSorted();

        assertEquals(50L, result.get(0).getMessagesSent());
        assertEquals(5L, result.get(1).getMessagesSent());
    }

    @Test
    void getManagerWorkloadsSorted_usesIdLabelWhenNoName() {
        User manager = new User();
        manager.setId(99);
        manager.setEmail("x@example.com");

        when(userRepository.findUserByRole(Role.MANAGER)).thenReturn(List.of(manager));
        when(messageChatRepository.aggregateStatsForManagers(Role.MANAGER)).thenReturn(List.of());

        List<ManagerWorkloadDto> result = adminDashboardService.getManagerWorkloadsSorted();

        assertEquals("#99", result.get(0).getDisplayName());
    }

    @Test
    void getManagerWorkloadsSorted_returnsEmptyWhenNoManagers() {
        when(userRepository.findUserByRole(Role.MANAGER)).thenReturn(List.of());
        when(messageChatRepository.aggregateStatsForManagers(Role.MANAGER)).thenReturn(List.of());

        List<ManagerWorkloadDto> result = adminDashboardService.getManagerWorkloadsSorted();

        assertTrue(result.isEmpty());
    }
}
