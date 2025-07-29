package com.iss.hdbPilot.model.vo;

import java.util.Date;

import lombok.Data;

@Data
public class PropertyVO {
    private Long id;

    private Long sellerId;

    private Date month;

    private String town;

    private String flatType;

    private String block;

    private String streetName;

    private String storeyRange;

    private Float floorAreaSqm;

    private Integer leaseCommenceDate;

    private String flatModel;
}
