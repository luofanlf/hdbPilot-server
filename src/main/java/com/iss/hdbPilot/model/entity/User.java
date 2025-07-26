package com.iss.hdbPilot.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iss.hdbPilot.model.vo.UserVO;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("users") // 对应数据库中的 users 表
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String email;

    @TableField("password_hash")
    private String passwordHash;

    private String nickname;

    @TableField("avatar_url")
    private String avatarUrl;

    private String bio;

    @TableField("user_role")
    private String userRole;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    public UserVO toVO(){
        UserVO userVO = new UserVO();
        userVO.setId(this.id);
        userVO.setUsername(this.username);
        userVO.setEmail(this.email);
        userVO.setNickname(this.nickname);
        userVO.setBio(this.bio);
        return userVO;
    }
}