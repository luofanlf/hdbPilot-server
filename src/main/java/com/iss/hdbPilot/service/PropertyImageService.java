package com.iss.hdbPilot.service;

import java.util.List;
import com.iss.hdbPilot.model.vo.PropertyImageVO;

public interface PropertyImageService {
    
    /**
     * 获取指定房源的所有图片
     */
    List<PropertyImageVO> getPropertyImages(Long propertyId);
    
    /**
     * 上传房源图片
     */
    PropertyImageVO uploadPropertyImage(Long propertyId, String imageUrl);
    
    /**
     * 删除房源图片
     */
    boolean deletePropertyImage(Long imageId);
    
    /**
     * 批量上传房源图片
     */
    List<PropertyImageVO> uploadPropertyImages(Long propertyId, List<String> imageUrls);
} 