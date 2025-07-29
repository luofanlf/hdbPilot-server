package com.iss.hdbPilot.service;

import java.util.List;

import com.iss.hdbPilot.model.dto.PropertyRequest;
import com.iss.hdbPilot.model.vo.PropertyVO;

public interface PropertyService {
    /**
     * 获取所有房源列表
     */
    List<PropertyVO> list();
    
    /**
     * 根据ID获取房源详情
     */
    PropertyVO getById(Long id);
    
   
    
    /**
     * 创建房源
     */
    PropertyVO create(PropertyRequest request);
    
    /**
     * 更新房源
     */
    PropertyVO update(Long id, PropertyRequest request);
    
    /**
     * 删除房源
     */
    boolean delete(Long id);
}
