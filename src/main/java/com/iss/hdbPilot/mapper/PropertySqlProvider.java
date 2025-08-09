package com.iss.hdbPilot.mapper;

import org.apache.ibatis.jdbc.SQL;

public class PropertySqlProvider {
    public String getMonthlyListingCounts(final Integer year) {
        return new SQL(){{
            SELECT("DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS count");
            FROM("property");
            if (year != null) {
                WHERE("YEAR(created_at) = #{year}");
            }
            GROUP_BY("month");
            ORDER_BY("month");
        }}.toString();
    }
}

