package com.iss.hdbPilot.model.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;                  // Primary key
    private Integer rating;           // Score
    private String content;           // Comment text
    private LocalDateTime createdAt;  // Time of creation
    private Long propertyId;  // 新增字段，用于关联房屋
    private Long userId; // 新增：关联用户ID

}
