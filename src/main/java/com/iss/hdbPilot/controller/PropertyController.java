package com.iss.hdbPilot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.service.PropertyService;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.dto.PropertyAddForm;
import com.iss.hdbPilot.model.dto.PropertyAddRequest;
import com.iss.hdbPilot.model.dto.PropertyQueryRequest;
import com.iss.hdbPilot.model.vo.PropertyVO;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/property")
public class PropertyController {
    
    @Autowired
    private PropertyService propertyService;

    /**
     * 获取所有房源列表
     */
    @GetMapping("/list")
    public BaseResponse<Page<PropertyVO>> list(PageRequest pageRequest){
        return ResultUtils.success(propertyService.list(pageRequest));
    }
    
    /**
     * 根据筛选条件搜索房源
     */
    @GetMapping("/search")
    public BaseResponse<Page<PropertyVO>> search(@Valid PropertyQueryRequest queryRequest){
        return ResultUtils.success(propertyService.search(queryRequest));
    }
    
    /**
     * 根据ID获取房源详情
     */
    @GetMapping("/{id}")
    public BaseResponse<PropertyVO> getById(@PathVariable Long id){
        PropertyVO property = propertyService.getById(id);
        if (property == null) {
            return new BaseResponse<>(-1, null, "房源不存在");
        }
        return ResultUtils.success(property);
    }
    
    /**
     * 创建新房源
     */
    @PostMapping
    public BaseResponse<PropertyVO> create(@ModelAttribute PropertyAddForm form){
            return ResultUtils.success(propertyService.create(form));
    }
    
    /**
     * 更新房源
     */
    @PutMapping("/{id}")
    public BaseResponse<PropertyVO> update(@PathVariable Long id, @RequestBody PropertyAddRequest request){
        try {
            return ResultUtils.success(propertyService.update(id, request));
        } catch (RuntimeException e) {
            return new BaseResponse<>(-1, null, e.getMessage());
        }
    }
    
    /**
     * 获取用户发布的房源列表
     */
    @GetMapping("/user/{sellerId}")
    public BaseResponse<List<PropertyVO>> getUserProperties(@PathVariable Long sellerId){
        try {
            return ResultUtils.success(propertyService.getUserProperties(sellerId));
        } catch (RuntimeException e) {
            return new BaseResponse<>(-1, null, e.getMessage());
        }
    }
    
    /**
     * 删除房源
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable Long id){
        boolean success = propertyService.delete(id);
        if (success) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(-1, false, "房源不存在或删除失败");
        }
    }
}
