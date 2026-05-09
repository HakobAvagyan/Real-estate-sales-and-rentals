package org.example.service;

import org.example.dto.admin.AdminPlatformOverviewDto;
import org.example.dto.admin.ManagerWorkloadDto;
import org.example.dto.admin.PropertyMarketStatsDto;

import java.util.List;

public interface AdminDashboardService {

    PropertyMarketStatsDto getPropertyMarketStats();

    AdminPlatformOverviewDto getPlatformOverview();

    List<ManagerWorkloadDto> getManagerWorkloadsSorted();
}
