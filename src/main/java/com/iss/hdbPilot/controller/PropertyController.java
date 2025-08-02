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
import com.iss.hdbPilot.model.vo.PropertyImageVO;

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
    public BaseResponse<Page<PropertyVO>> list(PropertyQueryRequest pageRequest){
        return ResultUtils.success(propertyService.list(pageRequest));
    }
    
    /**
     * 获取所有房源列表（不分页，用于地图显示）
     */
    @GetMapping("/list/all")
    public BaseResponse<List<PropertyVO>> listAll(){
        return ResultUtils.success(propertyService.listAll());
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
        return ResultUtils.success(propertyService.update(id, request));
    }
    
    /**
     * 获取用户发布的房源列表
     */
    @GetMapping("/user/{sellerId}")
    public BaseResponse<List<PropertyVO>> getUserProperties(@PathVariable Long sellerId){
        return ResultUtils.success(propertyService.getUserProperties(sellerId));
    }
    
    /**
     * 获取指定房源的所有图片
     */
    @GetMapping("/{propertyId}/images")
    public BaseResponse<List<PropertyImageVO>> getPropertyImages(@PathVariable Long propertyId){
        return ResultUtils.success(propertyService.getPropertyImages(propertyId));
    }
    
    /**
     * 为房源添加图片
     */
    @PostMapping("/{propertyId}/images")
    public BaseResponse<PropertyImageVO> addPropertyImage(
            @PathVariable Long propertyId,
            @RequestParam("imageFile") MultipartFile imageFile){
        return ResultUtils.success(propertyService.addPropertyImage(propertyId, imageFile));
    }
    
    /**
     * 删除房源图片
     */
    @DeleteMapping("/images/{imageId}")
    public BaseResponse<Boolean> deletePropertyImage(@PathVariable Long imageId){
        boolean success = propertyService.deletePropertyImage(imageId);
        if (success) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(-1, false, "图片不存在或删除失败");
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
