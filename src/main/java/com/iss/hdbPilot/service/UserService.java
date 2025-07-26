package com.iss.hdbPilot.service;

import javax.servlet.http.HttpServletRequest;

import com.iss.hdbPilot.model.entity.User;

public interface UserService {
    

    Long login(String username,String password,HttpServletRequest request);

    /**
     * 用户注册
     * @param userRegisterRequest
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
}
