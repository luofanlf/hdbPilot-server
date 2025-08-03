package com.iss.hdbPilot.service;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iss.hdbPilot.model.dto.UserUpdateRequest;
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

    boolean updateUser(UserUpdateRequest request);
}
