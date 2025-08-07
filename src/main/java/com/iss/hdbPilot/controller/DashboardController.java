package com.iss.hdbPilot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.dto.PropertyFilterRequest;
import com.iss.hdbPilot.model.dto.PropertyQueryRequest;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public BaseResponse<Page<PropertyVO>> listPendingProperties(@RequestBody PropertyFilterRequest request) {
        long current = request.getPageNum();
        long size = request.getPageSize();
        return ResultUtils.success(propertyService.listPendingPropertiesByPage(current, size, request));
    }

    @PostMapping("/review")
    public Boolean reviewProperty(@RequestBody Map<String, Object> request) {
        Long id = Long.valueOf(request.get("id").toString());
        Boolean approved = Boolean.valueOf(request.get("approved").toString());
        return propertyService.reviewProperty(id, approved);
    }
}
