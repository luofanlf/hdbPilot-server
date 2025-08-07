package com.iss.hdbPilot.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FavoriteRequest {
    
    @NotNull(message = "房源ID不能为空")
    private Long propertyId;
} 