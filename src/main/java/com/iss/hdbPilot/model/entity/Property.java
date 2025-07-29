package com.iss.hdbPilot.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("property")
public class Property implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 卖家ID
     */
    @TableField("seller_id")
    private Integer sellerId;

    /**
     * 月份
     */
    @TableField("month")
    private Date month;

    /**
     * 城镇
     */
    @TableField("town")
    private String town;

    /**
     * 公寓类型
     */
    @TableField("flat_type")
    private String flatType;

    /**
     * 座号
     */
    @TableField("block")
    private String block;

    /**
     * 街道名称
     */
    @TableField("street_name")
    private String streetName;

    /**
     * 楼层范围
     */
    @TableField("storey_range")
    private String storeyRange;

    /**
     * 建筑面积（平方米）
     */
    @TableField("floor_area_sqm")
    private Float floorAreaSqm;

    /**
     * 租约开始日期
     */
    @TableField("lease_commence_date")
    private Integer leaseCommenceDate;

    /**
     * 剩余租期
     */
    @TableField("remaining_lease")
    private String remainingLease;

    /**
     * 公寓模型
     */
    @TableField("flat_model")
    private String flatModel;

    /**
     * 转售价格
     */
    @TableField("resale_price")
    private Float resalePrice;

    /**
     * 预测价格
     */
    @TableField("forecast_price")
    private Float forecastPrice;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private Date createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private Date updatedAt;

}

