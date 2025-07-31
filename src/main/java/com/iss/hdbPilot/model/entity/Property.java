package com.iss.hdbPilot.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

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

    /**
     * 房源标题
     */
    @TableField("listing_title")
    private String listingTitle;

    /**
     * 卖家ID
     */
    @TableField("seller_id")
    private Long sellerId;

    /**
     * 城镇
     */
    @TableField("town")
    private String town;

    /**
     * 邮政编码
     */
    @TableField("postal_code")
    private String postalCode;

    /**
     * 卧室数量
     */
    @TableField("bedroom_number")
    private Integer bedroomNumber;

    /**
     * 浴室数量
     */
    @TableField("bathroom_number")
    private Integer bathroomNumber;

    /**
     * 楼栋
     */
    @TableField("block")
    private String block;

    /**
     * 街道名称
     */
    @TableField("street_name")
    private String streetName;

    /**
     * 楼层
     */
    @TableField("storey")
    private String storey;

    /**
     * 建筑面积（平方米）
     */
    @TableField("floor_area_sqm")
    private Float floorAreaSqm;

    /**
     * 顶层年份
     */
    @TableField("top_year")
    private Integer topYear;

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
     * 图片路径
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public PropertyVO toVO(){
        PropertyVO propertyVO = new PropertyVO();
        propertyVO.setId(this.id);
        propertyVO.setListingTitle(this.listingTitle);
        propertyVO.setSellerId(this.sellerId);
        propertyVO.setTown(this.town);
        propertyVO.setPostalCode(this.postalCode);
        propertyVO.setBedroomNumber(this.bedroomNumber);
        propertyVO.setBathroomNumber(this.bathroomNumber);
        propertyVO.setBlock(this.block);
        propertyVO.setStreetName(this.streetName);
        propertyVO.setStorey(this.storey);
        propertyVO.setFloorAreaSqm(this.floorAreaSqm);
        propertyVO.setTopYear(this.topYear);
        propertyVO.setFlatModel(this.flatModel);
        propertyVO.setResalePrice(this.resalePrice);
        propertyVO.setAvatarUrl(this.avatarUrl);
        propertyVO.setStatus(this.status);
        propertyVO.setCreatedAt(this.createdAt);
        propertyVO.setUpdatedAt(this.updatedAt);
        return propertyVO;
    }
}

