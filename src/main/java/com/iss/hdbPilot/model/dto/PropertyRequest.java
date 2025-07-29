package com.iss.hdbPilot.model.dto;

import lombok.Data;

@Data
public class PropertyRequest {
    /**
     * 房源标题
     */
    private String listingTitle;
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 城镇
     */
    private String town;
    
    /**
     * 邮政编码
     */
    private String postalCode;
    
    /**
     * 卧室数量
     */
    private Integer bedroomNumber;
    
    /**
     * 浴室数量
     */
    private Integer bathroomNumber;
    
    /**
     * 楼栋
     */
    private String block;
    
    /**
     * 街道名称
     */
    private String streetName;
    
    /**
     * 楼层
     */
    private String storey;
    
    /**
     * 建筑面积（平方米）
     */
    private Float floorAreaSqm;
    
    /**
     * 顶层年份
     */
    private Integer topYear;
    
    /**
     * 公寓模型
     */
    private String flatModel;
    
    /**
     * 转售价格
     */
    private Float resalePrice;
    
    /**
     * 预测价格
     */
    private Float forecastPrice;
    
    /**
     * 状态
     */
    private String status;
} 