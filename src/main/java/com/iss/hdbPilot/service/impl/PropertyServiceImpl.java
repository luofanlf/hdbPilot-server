package com.iss.hdbPilot.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.dto.PropertyAddRequest;
import com.iss.hdbPilot.model.dto.PropertyQueryRequest;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService{
    
    @Autowired
    private PropertyMapper propertyMapper;

    @Override
    public Page<PropertyVO> list(PropertyQueryRequest pageRequest) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        String listingTitle = pageRequest.getListingTitle();

        // 构造分页对象 - 先查询Question实体
        Page<Property> questionPage = new Page<>(pageNum, pageSize);

        // 构造查询条件
        LambdaQueryWrapper<Property> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(listingTitle)) {
            wrapper.like(Property::getListingTitle, listingTitle);
        }

        // 执行分页查询
        Page<Property> result = propertyMapper.selectPage(questionPage, wrapper);

        // 转换为QuestionVO
        Page<PropertyVO> propertyVOPage = new Page<>(pageNum, pageSize, result.getTotal());
        propertyVOPage.setRecords(result.getRecords().stream()
                .map(Property::toVO)
                .collect(java.util.stream.Collectors.toList()));

        return propertyVOPage; 
        // LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        // List<Property> properties = propertyMapper.selectList(null);
        // return properties.stream().map(Property::toVO).collect(Collectors.toList());
    }
    
    @Override
    public PropertyVO getById(Long id) {
        Property property = propertyMapper.selectById(id);
        return property != null ? property.toVO() : null;
    }
    
    @Override
    public List<PropertyVO> getUserProperties(Long sellerId) {
        QueryWrapper<Property> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("seller_id", sellerId);
        List<Property> properties = propertyMapper.selectList(queryWrapper);
        return properties.stream().map(Property::toVO).collect(Collectors.toList());
    }
    
    
    @Override
    public PropertyVO create(PropertyAddRequest request) {
        Property property = new Property();
        BeanUtils.copyProperties(request, property);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        
        propertyMapper.insert(property);
        return property.toVO();
    }
    
    @Override
    public PropertyVO update(Long id, PropertyAddRequest request) {
        Property existingProperty = propertyMapper.selectById(id);
        if (existingProperty == null) {
            throw new RuntimeException("房源不存在");
        }
        
        BeanUtils.copyProperties(request, existingProperty);
        existingProperty.setUpdatedAt(LocalDateTime.now());
        
        propertyMapper.updateById(existingProperty);
        return existingProperty.toVO();
    }
    
    @Override
    public boolean delete(Long id) {
        Property property = propertyMapper.selectById(id);
        if (property == null) {
            return false;
        }
        
        return propertyMapper.deleteById(id) > 0;
    }
}
