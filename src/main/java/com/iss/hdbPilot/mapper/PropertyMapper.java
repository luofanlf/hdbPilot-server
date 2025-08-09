package com.iss.hdbPilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iss.hdbPilot.model.dto.ListingStatusCount;
import com.iss.hdbPilot.model.dto.MonthlyListingCount;
import com.iss.hdbPilot.model.entity.Property;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

@Mapper
public interface PropertyMapper extends BaseMapper<Property> {
    @Select("SELECT COUNT(*) FROM property WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) AND YEAR(created_at) = YEAR(CURRENT_DATE())")
    long countThisMonth();

    @Select("SELECT COUNT(*) FROM property WHERE MONTH(created_at) = MONTH(CURRENT_DATE() - INTERVAL 1 MONTH) AND YEAR(created_at) = YEAR(CURRENT_DATE() - INTERVAL 1 MONTH)")
    long countLastMonth();

    @SelectProvider(type = PropertySqlProvider.class, method = "getMonthlyListingCounts")
    List<MonthlyListingCount> getMonthlyListingCounts(Integer year);


    @Select("SELECT status, COUNT(*) AS count FROM property GROUP BY status")
    List<ListingStatusCount> getStatusDistribution();

}