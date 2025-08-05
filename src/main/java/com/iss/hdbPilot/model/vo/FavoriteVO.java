package com.iss.hdbPilot.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FavoriteVO {
    
    private Long id;
    
    private Long userId;
    
    private Long propertyId;
    
    private PropertyVO property; // 关联的房源信息
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
} 