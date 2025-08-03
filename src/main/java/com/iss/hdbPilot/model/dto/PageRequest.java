package com.iss.hdbPilot.model.dto;
import lombok.Data;

@Data
public class PageRequest {
    private int pageNum = 1;   // 当前页码，默认第1页
    private int pageSize = 10; // 每页条数，默认10条
    private String keyword;
}