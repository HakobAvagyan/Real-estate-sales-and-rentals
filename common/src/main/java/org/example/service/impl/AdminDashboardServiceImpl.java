package org.example.service.impl;

import lombok.RequiredArgsConstructor;
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
import org.example.service.AdminDashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final MessageChatRepository messageChatRepository;
    private final CommentRepository commentRepository;
    private final RatingsRepository ratingsRepository;
    private final BookingRepository bookingRepository;

    @Override
    public PropertyMarketStatsDto getPropertyMarketStats() {
        long total = propertyRepository.count();
        long pendingMod = propertyRepository.countByModerationStatus(PropertyModerationStatus.PENDING);
        Map<PropertyStatus, Long> byStatus = new HashMap<>();
        for (PropertyStatus s : PropertyStatus.values()) {
            byStatus.put(s, 0L);
        }
        for (Object[] row : propertyRepository.countGroupedByStatus()) {
            PropertyStatus st = (PropertyStatus) row[0];
            byStatus.put(st, ((Number) row[1]).longValue());
        }
        Map<PropertyType, Long> byType = new HashMap<>();
        for (PropertyType t : PropertyType.values()) {
            byType.put(t, 0L);
        }
        for (Object[] row : propertyRepository.countGroupedByPropertyType()) {
            PropertyType t = (PropertyType) row[0];
            byType.put(t, ((Number) row[1]).longValue());
        }
        return PropertyMarketStatsDto.builder()
                .pendingModeration(pendingMod)
                .totalListings(total)
                .forSale(byStatus.getOrDefault(PropertyStatus.FOR_SALE, 0L))
                .forRent(byStatus.getOrDefault(PropertyStatus.FOR_RENT, 0L))
                .sold(byStatus.getOrDefault(PropertyStatus.SOLD, 0L))
                .rented(byStatus.getOrDefault(PropertyStatus.RENTED, 0L))
                .typeHouse(byType.getOrDefault(PropertyType.HOUSE, 0L))
                .typeApartment(byType.getOrDefault(PropertyType.APARTMENT, 0L))
                .typeLand(byType.getOrDefault(PropertyType.LAND, 0L))
                .build();
    }

    @Override
    public AdminPlatformOverviewDto getPlatformOverview() {
        return AdminPlatformOverviewDto.builder()
                .totalUsers(userRepository.count())
                .usersRoleUser(userRepository.countByRole(Role.USER))
                .usersRoleManager(userRepository.countByRole(Role.MANAGER))
                .usersRoleAdmin(userRepository.countByRole(Role.ADMIN))
                .blockedUsers(userRepository.countByIsBlockedTrue())
                .totalComments(commentRepository.count())
                .totalRatings(ratingsRepository.count())
                .totalBookings(bookingRepository.count())
                .build();
    }

    @Override
    public List<ManagerWorkloadDto> getManagerWorkloadsSorted() {
        Map<Integer, Object[]> byManagerId = new HashMap<>();
        for (Object[] row : messageChatRepository.aggregateStatsForManagers(Role.MANAGER)) {
            int mid = ((Number) row[0]).intValue();
            byManagerId.put(mid, row);
        }
        List<ManagerWorkloadDto> list = new ArrayList<>();
        for (User m : userRepository.findUserByRole(Role.MANAGER)) {
            Object[] row = byManagerId.get(m.getId());
            long messages = row != null ? ((Number) row[1]).longValue() : 0L;
            long chats = row != null ? ((Number) row[2]).longValue() : 0L;
            LocalDateTime last = null;
            if (row != null && row[3] != null) {
                last = (LocalDateTime) row[3];
            }
            list.add(ManagerWorkloadDto.builder()
                    .managerId(m.getId())
                    .displayName(displayName(m))
                    .email(m.getEmail() != null ? m.getEmail() : "")
                    .messagesSent(messages)
                    .chatsHandled(chats)
                    .lastMessageAt(last)
                    .build());
        }
        list.sort(Comparator.comparingLong(ManagerWorkloadDto::getMessagesSent).reversed());
        return list;
    }

    private static String displayName(User u) {
        String first = u.getName() != null ? u.getName().trim() : "";
        String last = u.getSurname() != null ? u.getSurname().trim() : "";
        String combined = (first + " " + last).trim();
        return !combined.isEmpty() ? combined : ("#" + u.getId());
    }
}
