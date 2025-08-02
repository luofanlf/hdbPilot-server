package com.iss.hdbPilot.model.dto;

import lombok.Data;


@Data
public class PropertyQueryRequest extends PageRequest{
    
    /**
     * 房源标题（模糊查询）
     */
    private String listingTitle;
    
    /**
     * 邮政编码
     */
    private String postalCode;
    
    /**
     * 卧室数量范围 - 最小值
     */
    private Integer bedroomNumberMin;
    
    /**
     * 卧室数量范围 - 最大值
     */
    private Integer bedroomNumberMax;
    
    /**
     * 浴室数量范围 - 最小值
     */
    private Integer bathroomNumberMin;
    
    /**
     * 浴室数量范围 - 最大值
     */
    private Integer bathroomNumberMax;
    
    /**
     * 楼层范围 - 最小值
     */
    private String storeyMin;
    
    /**
     * 楼层范围 - 最大值
     */
    private String storeyMax;
    
    /**
     * 建筑面积范围 - 最小值（平方米）
     */
    private Float floorAreaSqmMin;
    
    /**
     * 建筑面积范围 - 最大值（平方米）
     */
    private Float floorAreaSqmMax;
    
    /**
     * 顶层年份范围 - 最小值
     */
    private Integer topYearMin;
    
    /**
     * 顶层年份范围 - 最大值
     */
    private Integer topYearMax;
    
    /**
     * 转售价格范围 - 最小值
     */
    private Float resalePriceMin;
    
    /**
     * 转售价格范围 - 最大值
     */
    private Float resalePriceMax;
    
    /**
     * 城镇
     */
    private String town;
    
}
