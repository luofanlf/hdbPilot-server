package com.iss.hdbPilot.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DashboardStatsResponse {
    private long totalListings;
    private long activeListings;
    private long soldListings;
    private long pendingListings;
    private long totalUsers;
    private double listingGrowth;
    private List<Map<String, Object>> monthlySubmissions;
    private List<Map<String, Object>> approvalDistribution;
}