package com.iss.hdbPilot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iss.hdbPilot.mapper.UserMapper;
import com.iss.hdbPilot.model.dto.AdminUserUpdateRequest;
import com.iss.hdbPilot.model.entity.User;
import com.iss.hdbPilot.model.vo.UserVO;
import com.iss.hdbPilot.service.UserService;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private S3Client s3Client;

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
        if(!user.getUserRole().equals("admin")){
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
    public boolean updateUser(AdminUserUpdateRequest request) {
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
    // ======================== 新增的用户自我管理方法实现 ========================
    @Override
    public boolean updateUsername(Long userId, String newUsername) {
        if (userId == null || newUsername == null || newUsername.isEmpty()) {
            return false;
        }
        // 检查新用户名是否已存在
        if (userMapper.selectCount(new QueryWrapper<User>().eq("username", newUsername)) > 0) {
            return false;
        }
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setUsername(newUsername);
        userToUpdate.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(userToUpdate) > 0;
    }

    @Override
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null || newPassword.length() < 8) {
            return false;
        }
        User user = userMapper.selectById(userId);
        if (user == null || !user.getPasswordHash().equals(getEncryptedPassword(oldPassword))) {
            return false;
        }
        user.setPasswordHash(getEncryptedPassword(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean updateEmail(Long userId, String newEmail) {
        if (userId == null || newEmail == null || newEmail.isEmpty()) {
            return false;
        }
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setEmail(newEmail);
        userToUpdate.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(userToUpdate) > 0;
    }

    @Override
    public boolean updateNickname(Long userId, String newNickname) {
        if (userId == null || newNickname == null || newNickname.isEmpty()) {
            return false;
        }
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setNickname(newNickname);
        userToUpdate.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(userToUpdate) > 0;
    }

    @Override
    public boolean updateBio(Long userId, String newBio) {
        if (userId == null) {
            return false;
        }
        if (newBio == null) {
            newBio = ""; // 允许清空简介
        }
        User userToUpdate = new User();
        userToUpdate.setId(userId);
        userToUpdate.setBio(newBio);
        userToUpdate.setUpdatedAt(LocalDateTime.now());
        return userMapper.updateById(userToUpdate) > 0;
    }

    @Override
    public long countAllUsers() {
        return userMapper.selectCount(null);
    }

    @Override
    public Boolean updateAvatar(Long userId, MultipartFile imageFile) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // 构建图片名并上传到s3
        String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        try {
            System.out.println("开始上传图片到S3: " + imageName);
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket("hdb-pilot")
                    .key(imageName)
                    .contentType(imageFile.getContentType())
                    .build(),
                RequestBody.fromInputStream(imageFile.getInputStream(), imageFile.getSize())
            );
            System.out.println("图片上传到S3成功: " + imageName);
        } catch (Exception e) {
            System.err.println("图片上传到S3失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("upload image failed: " + e.getMessage());
        }

        // 构建图片url并插入数据库
        String imageUrl = "https://hdb-pilot.s3.ap-southeast-1.amazonaws.com/" + URLEncoder.encode(imageName, StandardCharsets.UTF_8);
        
        user.setAvatarUrl(imageUrl);
        
        try {
            userMapper.updateById(user);
            System.out.println("图片信息保存到数据库成功: " + imageUrl);
        } catch (Exception e) {
            System.err.println("图片信息保存到数据库失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("save image info failed: " + e.getMessage());
        }
        
        return true;
    }
}
