package com.iss.hdbPilot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.model.dto.PageRequest;
import com.iss.hdbPilot.model.dto.UserLoginRequest;
import com.iss.hdbPilot.model.dto.UserUpdateRequest;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @PostMapping("/list")
    public BaseResponse<Page<UserVO>> listUsers(@RequestBody PageRequest pageRequest) {
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        String keyword = pageRequest.getKeyword();
        Page<UserVO> userVOPage = userService.listUsersByPage(pageNum, pageSize, keyword);
        return ResultUtils.success(userVOPage);
    }

    /**
     * Admin: Delete multiple users by a list of user IDs.
     *
     * This endpoint allows the admin to delete a batch of users in one request.
     * Typically used for multi-select deletion in a user management interface.
     *
     * @param userIds the list of user IDs to be deleted (in JSON array format)
     * @return a success response if deletion is successful; otherwise, a failure message
     */
    @PostMapping("/delete-multiple")
    public BaseResponse<Boolean> deleteUsersBatch(@RequestBody List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new BaseResponse<>(-1, false, "User ID list is empty.");
        }

        boolean result = userService.removeUsersByIds(userIds);
        if (result) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(-1, false, "Failed to delete users.");
        }
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

    /**
     * Controller endpoint for updating a user's information.
     * Accepts a POST request with user update data and returns a success or failure response.
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest request) {
        if (request == null || request.getId() == null) {
            return new BaseResponse<>(-1, false, "User ID cannot be null");
        }
        // Basic parameter validation
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            return new BaseResponse<>(-1, false, "Username cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return new BaseResponse<>(-1, false, "Email cannot be empty");
        }

        boolean updated = userService.updateUser(request);

        if (updated) {
            return ResultUtils.success(true);
        } else {
            return new BaseResponse<>(-1, false, "Update failed. The user may not exist or there was a database error.");
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
