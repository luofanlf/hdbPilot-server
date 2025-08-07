package com.iss.hdbPilot.model.dto;

import lombok.Data;

@Data
public class PropertyFilterRequest {
    private int pageNum;
    private int pageSize;

    private String sellerId;
    private String address;
    private String town;
    private String bedroomNumber;
    private String bathroomNumber;
}
