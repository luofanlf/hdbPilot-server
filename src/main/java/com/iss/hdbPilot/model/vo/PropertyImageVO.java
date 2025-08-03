package com.iss.hdbPilot.model.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PropertyImageVO {
    private Long id;
    private Long propertyId;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 