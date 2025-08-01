package com.iss.hdbPilot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.service.PropertyImageService;
import com.iss.hdbPilot.model.vo.PropertyImageVO;

import java.util.List;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/property")
public class PropertyImageController {
    
    @Autowired
    private PropertyImageService propertyImageService;

    /**
     * 获取指定房源的所有图片
     */
    @GetMapping("/{propertyId}/images")
    public BaseResponse<List<PropertyImageVO>> getPropertyImages(@PathVariable Long propertyId) {
        try {
            List<PropertyImageVO> images = propertyImageService.getPropertyImages(propertyId);
            return ResultUtils.success(images);
        } catch (RuntimeException e) {
            return new BaseResponse<>(-1, null, e.getMessage());
        }
    }
    
    /**
     * 上传房源图片
     */
    @PostMapping("/{propertyId}/images")
    public BaseResponse<PropertyImageVO> uploadPropertyImage(
            @PathVariable Long propertyId,
            @RequestParam("imageUrl") String imageUrl) {
        try {
            PropertyImageVO image = propertyImageService.uploadPropertyImage(propertyId, imageUrl);
            return ResultUtils.success(image);
        } catch (RuntimeException e) {
            return new BaseResponse<>(-1, null, e.getMessage());
        }
    }
    
    /**
     * 批量上传房源图片
     */
    @PostMapping("/{propertyId}/images/batch")
    public BaseResponse<List<PropertyImageVO>> uploadPropertyImages(
            @PathVariable Long propertyId,
            @RequestBody List<String> imageUrls) {
        try {
            List<PropertyImageVO> images = propertyImageService.uploadPropertyImages(propertyId, imageUrls);
            return ResultUtils.success(images);
        } catch (RuntimeException e) {
            return new BaseResponse<>(-1, null, e.getMessage());
        }
    }
    

    
    /**
     * 删除房源图片
     */
    @DeleteMapping("/images/{imageId}")
    public BaseResponse<Boolean> deletePropertyImage(@PathVariable Long imageId) {
        try {
            boolean success = propertyImageService.deletePropertyImage(imageId);
            if (success) {
                return ResultUtils.success(true);
            } else {
                return new BaseResponse<>(-1, false, "图片不存在或删除失败");
            }
        } catch (RuntimeException e) {
            return new BaseResponse<>(-1, false, e.getMessage());
        }
    }
} 