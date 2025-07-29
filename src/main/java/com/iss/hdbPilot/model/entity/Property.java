package com.iss.hdbPilot.model.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iss.hdbPilot.model.vo.PropertyVO;

import lombok.Data;

@Data
@TableName("property")
public class Property implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @TableField("seller_id")
    private Long sellerId;

    /**
     * Month of sale
     */
    @TableField("month")
    private Date month;

    /**
     * Designated residential area with its own amenities, infrastructure, and community facilities
     */
    @TableField("town")
    private String town;

    /**
     * Classification of units by room size. They range from 2 to 5 rooms, 3Gen units, and Executive units.
     */
    @TableField("flat_type")
    private String flatType;

    /**
     * A HDB building comprising multiple flats or apartments
     */
    @TableField("block")
    private String block;

    /**
     * Name of the road the HDB flat is located along
     */
    @TableField("street_name")
    private String streetName;

    /**
     * Estimated range of floors the unit sold was located on
     */
    @TableField("storey_range")
    private String storeyRange;

    /**
     * Total interior space within the unit, measured in square meters
     */
    @TableField("floor_area_sqm")
    private Float floorAreaSqm;

    /**
     * Classification of units by generation of which the flat was made, ranging from New Generation, DBSS, Improved, Apartment
     */
    @TableField("lease_commence_date")
    private Integer leaseCommenceDate;

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

    public PropertyVO toVO(){
        PropertyVO propertyVO = new PropertyVO();
        propertyVO.setId(this.id);
        propertyVO.setSellerId(this.sellerId);
        propertyVO.setMonth(this.month);
        propertyVO.setTown(this.town);
        propertyVO.setFlatType(this.flatType);
        propertyVO.setBlock(this.block);
        propertyVO.setStreetName(this.streetName);
        propertyVO.setStoreyRange(this.storeyRange);
        propertyVO.setFloorAreaSqm(this.floorAreaSqm);
        propertyVO.setLeaseCommenceDate(this.leaseCommenceDate);
        propertyVO.setFlatModel(this.flatModel);

        return propertyVO;
    }
}

