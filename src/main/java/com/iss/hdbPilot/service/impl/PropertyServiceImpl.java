package com.iss.hdbPilot.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iss.hdbPilot.mapper.PropertyMapper;
import com.iss.hdbPilot.model.dto.PropertyRequest;
import com.iss.hdbPilot.model.entity.Property;
import com.iss.hdbPilot.model.vo.PropertyVO;
import com.iss.hdbPilot.service.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService{
    
    @Autowired
    private PropertyMapper propertyMapper;

    @Override
    public List<PropertyVO> list() {
        List<Property> properties = propertyMapper.selectList(null);
        return properties.stream().map(Property::toVO).collect(Collectors.toList());
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
    public PropertyVO create(PropertyRequest request) {
        Property property = new Property();
        BeanUtils.copyProperties(request, property);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        
        propertyMapper.insert(property);
        return property.toVO();
    }
    
    @Override
    public PropertyVO update(Long id, PropertyRequest request) {
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
