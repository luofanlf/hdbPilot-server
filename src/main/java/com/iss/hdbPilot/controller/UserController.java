package com.iss.hdbPilot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.model.dto.UserLoginRequest;
import com.iss.hdbPilot.model.dto.UserRegisterRequest;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.UserService;



import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public BaseResponse<Long> login(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {
        if(loginRequest == null){
            throw new RuntimeException("Request body is null");
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        return ResultUtils.success(userService.login(username,password,request));
    }

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null){
            throw new RuntimeException("Request body is null");
        }
        String username = userRegisterRequest.getUsername();
        String password = userRegisterRequest.getPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        return ResultUtils.success(userService.register(username,password,confirmPassword));
    }

    
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return ResultUtils.success(true);
    }

    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request){
        UserVO userVO = userService.getCurrentUser(request).toVO();
        return ResultUtils.success(userVO);
    }
}
