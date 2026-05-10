package org.example.dto.admin;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdminPlatformOverviewDto {
    long totalUsers;
    long usersRoleUser;
    long usersRoleManager;
    long usersRoleAdmin;
    long blockedUsers;
    long totalComments;
    long totalRatings;
    long totalBookings;
}
