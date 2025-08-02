package com.iss.hdbPilot.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.dto.PropertyAddForm;
import com.iss.hdbPilot.model.dto.PropertyAddRequest;
import com.iss.hdbPilot.model.dto.PropertyQueryRequest;
import com.iss.hdbPilot.model.vo.PropertyVO;

public interface PropertyService {
    /**
     * 获取所有房源列表
     */
    Page<PropertyVO> list(PropertyQueryRequest pageRequest);
    
    /**
     * 根据ID获取房源详情
     */
    PropertyVO getById(Long id);
    
    /**
     * 获取用户发布的房源列表
     */
    List<PropertyVO> getUserProperties(Long sellerId);
    
   
    
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
}
