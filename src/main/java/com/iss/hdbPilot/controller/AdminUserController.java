package com.iss.hdbPilot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.annotation.AuthCheck;
import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.dto.UserLoginRequest;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {

    @Resource
    private UserService userService;

    /**
     * Admin: Get paginated list of all users (excluding admins).
     *
     * @param pageRequest the pagination request containing pageNum and pageSize
     * @return paginated list of UserVO objects
     */
    @GetMapping("/list")
    public BaseResponse<Page<UserVO>> listUsers(@RequestBody PageRequest pageRequest) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        Page<UserVO> userVOPage = userService.listUsersByPage(pageNum, pageSize);
        return ResultUtils.success(userVOPage);
    }

    /**
     * Admin: Delete a user by user ID.
     *
     * @param userId the ID of the user to delete
     * @return true if deletion was successful, false otherwise
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteUser(@PathVariable("id") Long userId) {
        boolean result = userService.removeUserById(userId);
        if (result) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(-1, false, "Failed to delete user. User may not exist or database error occurred.");
        }
    }

    @PostMapping("/login")
    public BaseResponse<Long> login(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {
        if(loginRequest == null){
            throw new RuntimeException("Request body is null");
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        return ResultUtils.success(userService.adminLogin(username,password,request));
    }

}
