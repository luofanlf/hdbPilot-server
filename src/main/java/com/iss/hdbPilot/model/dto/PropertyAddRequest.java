package com.iss.hdbPilot.model.dto;

import lombok.Data;
import javax.validation.constraints.*;

@Data
public class PropertyAddRequest {
    /**
     * 房源标题
     */
    @NotBlank(message = "房源标题不能为空")
    @Size(max = 255, message = "房源标题长度不能超过255个字符")
    private String listingTitle;
    
    /**
     * 卖家ID
     */
    @NotNull(message = "卖家ID不能为空")
    @Min(value = 1, message = "卖家ID必须大于0")
    private Long sellerId;
    
    /**
     * 城镇
     */
    @NotBlank(message = "城镇不能为空")
    @Size(max = 255, message = "城镇名称长度不能超过255个字符")
    private String town;
    
    /**
     * 邮政编码
     */
    @NotBlank(message = "邮政编码不能为空")
    @Pattern(regexp = "^[0-9]{6}$", message = "邮政编码必须是6位数字")
    private String postalCode;
    
    /**
     * 卧室数量
     */
    @NotNull(message = "卧室数量不能为空")
    @Min(value = 1, message = "卧室数量必须大于0")
    @Max(value = 10, message = "卧室数量不能超过10")
    private Integer bedroomNumber;
    
    /**
     * 浴室数量
     */
    @NotNull(message = "浴室数量不能为空")
    @Min(value = 1, message = "浴室数量必须大于0")
    @Max(value = 5, message = "浴室数量不能超过5")
    private Integer bathroomNumber;
    
    /**
     * 楼栋
     */
    @NotBlank(message = "楼栋不能为空")
    @Size(max = 50, message = "楼栋名称长度不能超过50个字符")
    private String block;
    
    /**
     * 街道名称
     */
    @NotBlank(message = "街道名称不能为空")
    @Size(max = 255, message = "街道名称长度不能超过255个字符")
    private String streetName;
    
    /**
     * 楼层
     */
    @NotBlank(message = "楼层不能为空")
    @Size(max = 50, message = "楼层信息长度不能超过50个字符")
    private String storey;
    
    /**
     * 建筑面积（平方米）
     */
    @NotNull(message = "建筑面积不能为空")
    @Min(value = 20, message = "建筑面积必须大于20平方米")
    @Max(value = 200, message = "建筑面积不能超过200平方米")
    private Float floorAreaSqm;
    
    /**
     * 顶层年份
     */
    @NotNull(message = "顶层年份不能为空")
    @Min(value = 1960, message = "顶层年份不能早于1960年")
    @Max(value = 2024, message = "顶层年份不能晚于2024年")
    private Integer topYear;
    
    /**
     * 公寓模型
     */
    @NotBlank(message = "公寓模型不能为空")
    @Size(max = 255, message = "公寓模型名称长度不能超过255个字符")
    private String flatModel;
    
    /**
     * 转售价格
     */
    @NotNull(message = "转售价格不能为空")
    @Min(value = 100000, message = "转售价格必须大于10万")
    @Max(value = 2000000, message = "转售价格不能超过200万")
    private Float resalePrice;
    
    /**
     * 预测价格
     */
    private Float forecastPrice;


    /**
     * 状态
     */
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^(available|sold)$", message = "状态只能是available或sold")
    private String status;
} 