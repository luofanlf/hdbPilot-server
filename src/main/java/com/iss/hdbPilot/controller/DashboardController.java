package com.iss.hdbPilot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/property")
public class DashboardController {

    @Autowired
    private PropertyService propertyService;

    /**
     * Paginated query of pending property listings
     */
    @PostMapping("/list_pending")
    public Page<PropertyVO> listPendingProperties(@RequestBody PageRequest request) {
        long current = request.getPageNum();
        long size = request.getPageSize();
        String keyword = request.getKeyword();
        return propertyService.listPendingPropertiesByPage(current, size, keyword);
    }

    @PostMapping("/review")
    public Boolean reviewProperty(@RequestBody Map<String, Object> request) {
        Long id = Long.valueOf(request.get("id").toString());
        Boolean approved = Boolean.valueOf(request.get("approved").toString());
        return propertyService.reviewProperty(id, approved);
    }


}
