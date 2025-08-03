package com.iss.hdbPilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.dto.UserUpdateRequest;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.UserService;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long login(String username,String password,HttpServletRequest request) {
        //校验参数
        if(username == null || password == null){
            throw new RuntimeException("Username and password cannot be null");
        }
        //查询用户是否存在
        String encryptedPassword = this.getEncryptedPassword(password);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        queryWrapper.eq("password_hash",encryptedPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            throw new RuntimeException("username or password is incorrect");
        }
        //存储用户登陆态
        request.getSession().setAttribute("user",user);

        //返回用户id
        return user.getId();
    }

    @Override
    public Long adminLogin(String username,String password,HttpServletRequest request){
        //校验参数
        if(username == null || password == null){
            throw new RuntimeException("Username and password cannot be null");
        }
        //查询用户是否存在
        String encryptedPassword = this.getEncryptedPassword(password);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        queryWrapper.eq("password_hash",encryptedPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            throw new RuntimeException("username or password is incorrect");
        }
        if(user.getUserRole() != "admin"){
            throw new RuntimeException("user is not admin");
        }
        //存储用户登陆态
        request.getSession().setAttribute("user",user);

        //返回用户id
        return user.getId();
        
    }

    @Override
    public Long register(String username,String password,String confirmPassword){
    
        //校验注册参数
        if(username == null || password == null || confirmPassword == null){
            throw new RuntimeException("Username and password cannot be null");
        }
        if(username.length() < 4 || username.length() > 16){
            throw new RuntimeException("Username must be between 4 and 16 characters long");
        }
        if(password.length() < 8){
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        if(!password.equals(confirmPassword)){
            throw new RuntimeException("Passwords do not match");
        }

        //校验账号是否重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0){
            throw new RuntimeException("Username already exists");
        }

        //加密密码
        String encryptedPassword = getEncryptedPassword(password);

        //创建用户
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encryptedPassword);
        user.setUserRole("user");
        userMapper.insert(user);

        return user.getId();
        
    }
    
    @Override
    public User getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute("user");
        if(userObj == null){
            throw new RuntimeException("User not found");
        }
        User user = (User) userObj;
        return user;
    }


    @Override
    public String getEncryptedPassword(String password){
        final String SALT = "luofan";
        return DigestUtils.md5DigestAsHex((SALT + password).getBytes());
    }


    @Override
    public Page<UserVO> listUsersByPage(long current, long size, String keyword) {
        Page<User> page = new Page<>(current, size);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("user_role", "admin"); // 排除管理员

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            queryWrapper.and(wrapper ->
                    wrapper.like("username", kw)
                            .or()
                            .like("email", kw)
            );
        }

        Page<User> userPage = userMapper.selectPage(page, queryWrapper);

        Page<UserVO> userVOPage = new Page<>();
        userVOPage.setCurrent(current);
        userVOPage.setSize(size);
        userVOPage.setTotal(userPage.getTotal());

        List<UserVO> voList = userPage.getRecords().stream()
                .map(User::toVO)
                .collect(Collectors.toList());
        userVOPage.setRecords(voList);
        return userVOPage;
    }


    @Override
    public boolean removeUserById(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID is illegal");
        }
        return userMapper.deleteById(userId) > 0;
    }

    @Override
    public boolean removeUsersByIds(List<Long> userIds) {
        return userMapper.deleteBatchIds(userIds) > 0;
    }

    @Override
    public boolean updateUser(UserUpdateRequest request) {
        // 使用 UpdateWrapper 进行更新
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", request.getId());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname());

        // update(user, wrapper) 返回boolean
        return this.update(user, updateWrapper);
    }

}
