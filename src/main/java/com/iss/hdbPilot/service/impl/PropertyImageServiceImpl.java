package com.iss.hdbPilot.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iss.hdbPilot.mapper.PropertyImageMapper;
import com.iss.hdbPilot.model.entity.PropertyImage;
import com.iss.hdbPilot.model.vo.PropertyImageVO;
import com.iss.hdbPilot.service.PropertyImageService;

@Service
public class PropertyImageServiceImpl implements PropertyImageService {
    
    @Autowired
    private PropertyImageMapper propertyImageMapper;

    @Override
    public List<PropertyImageVO> getPropertyImages(Long propertyId) {
        QueryWrapper<PropertyImage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("property_id", propertyId);
        queryWrapper.orderByAsc("created_at");
        
        List<PropertyImage> images = propertyImageMapper.selectList(queryWrapper);
        
        return images.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PropertyImageVO uploadPropertyImage(Long propertyId, String imageUrl) {
        PropertyImage propertyImage = new PropertyImage();
        propertyImage.setPropertyId(propertyId);
        propertyImage.setImageUrl(imageUrl);
        propertyImage.setCreatedAt(LocalDateTime.now());
        propertyImage.setUpdatedAt(LocalDateTime.now());
        
        propertyImageMapper.insert(propertyImage);
        
        return convertToVO(propertyImage);
    }
    
    @Override
    public boolean deletePropertyImage(Long imageId) {
        PropertyImage propertyImage = propertyImageMapper.selectById(imageId);
        if (propertyImage == null) {
            return false;
        }
        
        return propertyImageMapper.deleteById(imageId) > 0;
    }
    
    @Override
    public List<PropertyImageVO> uploadPropertyImages(Long propertyId, List<String> imageUrls) {
        List<PropertyImage> propertyImages = imageUrls.stream()
                .map(imageUrl -> {
                    PropertyImage propertyImage = new PropertyImage();
                    propertyImage.setPropertyId(propertyId);
                    propertyImage.setImageUrl(imageUrl);
                    propertyImage.setCreatedAt(LocalDateTime.now());
                    propertyImage.setUpdatedAt(LocalDateTime.now());
                    return propertyImage;
                })
                .collect(Collectors.toList());
        
        // 批量插入图片
        for (PropertyImage propertyImage : propertyImages) {
            propertyImageMapper.insert(propertyImage);
        }
        
        return propertyImages.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    private PropertyImageVO convertToVO(PropertyImage propertyImage) {
        PropertyImageVO vo = new PropertyImageVO();
        vo.setId(propertyImage.getId());
        vo.setPropertyId(propertyImage.getPropertyId());
        vo.setImageUrl(propertyImage.getImageUrl());
        vo.setCreatedAt(propertyImage.getCreatedAt());
        vo.setUpdatedAt(propertyImage.getUpdatedAt());
        return vo;
    }
} 