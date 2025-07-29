package com.iss.hdbPilot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.model.dto.UserLoginRequest;
import com.iss.hdbPilot.model.dto.UserRegisterRequest;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.PropertyService;
import com.iss.hdbPilot.service.UserService;
import com.iss.hdbPilot.model.vo.PropertyVO;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/property")
public class PropertyController {
    
    @Autowired
    private PropertyService propertyService;

    @GetMapping("/list")
    public BaseResponse<List<PropertyVO>> list(){
        return ResultUtils.success(propertyService.list());
    }
}
