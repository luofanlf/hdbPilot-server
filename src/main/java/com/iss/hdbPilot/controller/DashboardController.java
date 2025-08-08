package com.iss.hdbPilot.controller;

import com.iss.hdbPilot.model.dto.DashboardStatsResponse;
import com.iss.hdbPilot.model.dto.ListingStatusCount;
import com.iss.hdbPilot.model.dto.MonthlyListingCount;
import com.iss.hdbPilot.service.PropertyService;
import com.iss.hdbPilot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        long totalListings = propertyService.countAll();
        long activeListings = propertyService.countByStatus("available");
        long soldListings = propertyService.countByStatus("sold");
        long pendingListings = propertyService.countByStatus("pending");
        long totalUsers = userService.countAllUsers();
        Double listingGrowth = propertyService.calculateListingGrowth();

        Map<String, Object> response = new HashMap<>();
        response.put("totalListings", totalListings);
        response.put("activeListings", activeListings);
        response.put("soldListings", soldListings);
        response.put("pendingListings", pendingListings);
        response.put("totalUsers", totalUsers);
        response.put("listingGrowth", listingGrowth);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/charts")
    public ResponseEntity<?> getChartData(@RequestParam(required = false) Integer year) {
        List<MonthlyListingCount> monthlyCounts = propertyService.getMonthlyListingCounts(year);
        List<ListingStatusCount> statusCounts = propertyService.getStatusDistribution();

        Map<String, Object> response = new HashMap<>();
        response.put("monthlyCounts", monthlyCounts);
        response.put("statusCounts", statusCounts);

        return ResponseEntity.ok(response);
    }
}
