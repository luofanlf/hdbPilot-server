package com.iss.hdbPilot.model.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("property_image")
public class PropertyImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("property_id")
    private Long propertyId;

    @TableField("image_url")
    private String imageUrl;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
