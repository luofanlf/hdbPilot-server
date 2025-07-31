package com.iss.hdbPilot.model.vo;

import java.time.LocalDateTime;
import java.util.List;

import com.iss.hdbPilot.model.entity.PropertyImage;

import lombok.Data;

@Data
public class PropertyVO {
    private Long id;

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
     * 房屋图片
     */
    private List<PropertyImage> imageList;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
