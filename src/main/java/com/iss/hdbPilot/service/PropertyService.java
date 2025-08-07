package com.iss.hdbPilot.service;

import java.util.List;

import com.iss.hdbPilot.model.dto.*;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.model.vo.PropertyImageVO;

public interface PropertyService {
    /**
     * 获取所有房源列表
     */
    Page<PropertyVO> list(PageRequest pageRequest);
    
    /**
     * 根据筛选条件查询房源列表
     */
    Page<PropertyVO> search(PropertyQueryRequest queryRequest);
    
    /**
     * 获取所有房源列表（不分页，用于地图显示）
     */
    List<PropertyVO> listAll();
    
    /**
     * 根据ID获取房源详情
     */
    PropertyVO getById(Long id);
    
    /**
     * 获取用户发布的房源列表
     */
    List<PropertyVO> getUserProperties(Long sellerId);
    
    /**
     * 获取指定房源的所有图片
     */
    List<PropertyImageVO> getPropertyImages(Long propertyId);
    
    /**
     * 为房源添加图片
     */
    PropertyImageVO addPropertyImage(Long propertyId, MultipartFile imageFile);
    
    /**
     * 删除房源图片
     */
    boolean deletePropertyImage(Long imageId);
    
    /**
     * 创建房源
     */
    PropertyVO create(PropertyAddForm form);
    
    /**
     * 更新房源
     */
    PropertyVO update(Long id, PropertyAddRequest request);
    
    /**
     * 删除房源
     */
    boolean delete(Long id);

    /**
     *
     * @param pageNum
     * @param pageSize
     * @param filter
     * @return
     */
    Page<PropertyVO> listPendingPropertiesByPage(long pageNum, long pageSize, PropertyFilterRequest filter);


    /**
     * @param id
     * @param approved
     * @return
     */
    Boolean reviewProperty(Long id, Boolean approved);
}
