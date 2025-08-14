package com.iss.hdbPilot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.iss.hdbPilot.common.BaseResponse;
import com.iss.hdbPilot.common.ResultUtils;
import com.iss.hdbPilot.model.dto.UserLoginRequest;
import com.iss.hdbPilot.model.dto.UserRegisterRequest;
import com.iss.hdbPilot.model.dto.UserUpdateRequest; // 新增导入
import com.iss.hdbPilot.model.entity.User; // 新增导入
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 保持原样，不作修改
    @PostMapping("/login")
    public BaseResponse<Long> login(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {
        if(loginRequest == null){
            throw new RuntimeException("Request body is null");
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        return ResultUtils.success(userService.login(username,password,request));
    }

    // 保持原样，不作修改
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        if(userRegisterRequest == null){
            throw new RuntimeException("Request body is null");
        }
        String username = userRegisterRequest.getUsername();
        String email= userRegisterRequest.getEmail();
        String password = userRegisterRequest.getPassword();
        String confirmPassword = userRegisterRequest.getConfirmPassword();
        return ResultUtils.success(userService.register(username,password,confirmPassword,email));
    }

    // 保持原样，不作修改
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return ResultUtils.success(true);
    }

    // 保持原样，不作修改
    @GetMapping("/current")
    public BaseResponse<UserVO> getCurrentUser(HttpServletRequest request){
        UserVO userVO = userService.getCurrentUser(request).toVO();
        return ResultUtils.success(userVO);
    }

    @GetMapping("/profile")
    public BaseResponse<UserVO> getUserProfile(HttpServletRequest request) {
        User currentUser = userService.getCurrentUser(request);
        if (currentUser == null) {
            // 如果会话中没有用户，返回未登录错误
            return ResultUtils.error(401, "User not logged in");
        }
        // 将 User 实体转换为 UserVO，然后返回
        return ResultUtils.success(currentUser.toVO());
    }

    // ======================== 新增的用户自我管理方法 ========================
    /**
     * 允许用户修改自己的信息（用户名、密码、邮箱、昵称、个人简介）
     * @param updateRequest 包含要更新的信息
     * @param request HTTP请求，用于获取当前用户
     * @return 更新结果
     */

    @PostMapping("/update_profile")
    public BaseResponse<Boolean> updateUserProfile(@RequestBody UserUpdateRequest updateRequest, HttpServletRequest request) {
        // 获取当前登录的用户
        User currentUser = userService.getCurrentUser(request);
        if (currentUser == null) {
            return ResultUtils.error(401, "User not logged in"); // 未登录
        }

        Long userId = currentUser.getId();
        boolean isUpdated = false;

        // 修改用户名
        if (updateRequest.getNewUsername() != null && !updateRequest.getNewUsername().isEmpty()) {
            if (userService.updateUsername(userId, updateRequest.getNewUsername())) {
                isUpdated = true;
                // 更新Session中的用户信息，避免前端需要重新登录
                currentUser.setUsername(updateRequest.getNewUsername());
                request.getSession().setAttribute("user", currentUser);
            } else {
                return ResultUtils.error(400, "Failed to update username. It might already exist or be invalid.");
            }
        }

        // 修改密码
        if (updateRequest.getOldPassword() != null && updateRequest.getNewPassword() != null) {
            if (userService.updatePassword(userId, updateRequest.getOldPassword(), updateRequest.getNewPassword())) {
                isUpdated = true;
            } else {
                return ResultUtils.error(400, "Failed to update password. Old password is not correct or new password is invalid.");
            }
        }

        // 修改邮箱
        if (updateRequest.getNewEmail() != null && !updateRequest.getNewEmail().isEmpty()) {
            if (userService.updateEmail(userId, updateRequest.getNewEmail())) {
                isUpdated = true;
                currentUser.setEmail(updateRequest.getNewEmail());
                request.getSession().setAttribute("user", currentUser);
            } else {
                return ResultUtils.error(400, "Failed to update email.");
            }
        }

        // 修改昵称
        if (updateRequest.getNewNickname() != null && !updateRequest.getNewNickname().isEmpty()) {
            if (userService.updateNickname(userId, updateRequest.getNewNickname())) {
                isUpdated = true;
                currentUser.setNickname(updateRequest.getNewNickname());
                request.getSession().setAttribute("user", currentUser);
            } else {
                return ResultUtils.error(400, "Failed to update nickname.");
            }
        }

        // 修改个人简介
        if (updateRequest.getNewBio() != null) { // 允许将简介设为空
            if (userService.updateBio(userId, updateRequest.getNewBio())) {
                isUpdated = true;
                currentUser.setBio(updateRequest.getNewBio());
                request.getSession().setAttribute("user", currentUser);
            } else {
                return ResultUtils.error(400, "Failed to update bio.");
            }
        }

        // 如果至少有一项更新成功，或请求为空，则返回成功
        if (isUpdated || (updateRequest.getNewUsername() == null && updateRequest.getNewEmail() == null &&
                updateRequest.getNewNickname() == null && updateRequest.getNewBio() == null &&
                updateRequest.getOldPassword() == null && updateRequest.getNewPassword() == null)) {
            return ResultUtils.success(true);
        }

        return ResultUtils.error(400, "No valid fields provided for update.");
    }

    @PutMapping("{userId}/avatar")
    public BaseResponse<String> updateAvatar(@PathVariable Long userId,
    @RequestParam("imageFile") MultipartFile imageFile) {
        return ResultUtils.success(userService.updateAvatar(userId, imageFile));
    }

}