package com.iss.hdbPilot.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.dto.AdminUserUpdateRequest;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.UserVO;

import java.util.List;

public interface UserService {
    

    /**
     * 用户登录
     * @param username
     * @param password
     * @param request
     * @return
     */
    Long login(String username,String password,HttpServletRequest request);


    /**
     * 管理员登录
     * @param username
     * @param password
     * @param request
     * @return
     */
    Long adminLogin(String username,String password,HttpServletRequest request);
    
    /**
     * 用户注册
     * @param username
     * @param password
     * @param confirmPassword
     * @return
     */
    Long register(String username,String password,String confirmPassword);

    /**
     * 获取加密后的密码
     * @param password
     * @return
     */
    String getEncryptedPassword(String password);

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * Retrieves a paginated list of non-admin users (where user_role != 'admin').
     *
     * @param current the current page number (starting from 1)
     * @param size the number of users per page
     * @return a page of UserVO objects representing the users
     */
    Page<UserVO> listUsersByPage(long current, long size, String keyword);

    /**
     * Deletes a user by their ID. Typically used by an administrator.
     *
     * @param userId the ID of the user to delete
     * @return true if the user was successfully deleted, false otherwise
     */
    boolean removeUserById(Long userId);

    boolean removeUsersByIds(List<Long> userIds);

    boolean updateUser(AdminUserUpdateRequest request);

// ======================== 新增的用户自我管理方法 ========================
    /**
     * 更新用户的用户名
     * @param userId 用户的ID
     * @param newUsername 新的用户名
     * @return true 如果更新成功，false 如果用户名已存在或更新失败
     */
    boolean updateUsername(Long userId, String newUsername);

    /**
     * 更新用户的密码
     * @param userId 用户的ID
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文）
     * @return true 如果更新成功，false 如果旧密码不匹配或更新失败
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新用户的邮箱
     * @param userId 用户的ID
     * @param newEmail 新的邮箱
     * @return true 如果更新成功，false 如果更新失败
     */
    boolean updateEmail(Long userId, String newEmail);

    /**
     * 更新用户的昵称
     * @param userId 用户的ID
     * @param newNickname 新的昵称
     * @return true 如果更新成功，false 如果更新失败
     */
    boolean updateNickname(Long userId, String newNickname);

    /**
     * 更新用户的个人简介
     * @param userId 用户的ID
     * @param newBio 新的个人简介
     * @return true 如果更新成功，false 如果更新失败
     */
    boolean updateBio(Long userId, String newBio);

    /**
     *
     * @return
     */
    long countAllUsers();

    /**
     * 更新用户的头像
     * @param userId
     * @param imageFile
     * @return
     */
    Boolean updateAvatar(Long userId, MultipartFile imageFile);
}
