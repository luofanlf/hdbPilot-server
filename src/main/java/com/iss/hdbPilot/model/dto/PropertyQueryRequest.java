package com.iss.hdbPilot.model.dto;

import lombok.Data;

@Data
public class PropertyQueryRequest extends PageRequest{
    private String listingTitle;
}
