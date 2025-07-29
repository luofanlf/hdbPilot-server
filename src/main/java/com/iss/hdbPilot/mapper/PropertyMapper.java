package com.iss.hdbPilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iss.hdbPilot.model.entity.Property;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PropertyMapper extends BaseMapper<Property> {
}