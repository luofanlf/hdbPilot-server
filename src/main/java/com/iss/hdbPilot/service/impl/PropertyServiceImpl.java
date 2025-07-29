package com.iss.hdbPilot.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iss.hdbPilot.mapper.PropertyMapper;
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
}
