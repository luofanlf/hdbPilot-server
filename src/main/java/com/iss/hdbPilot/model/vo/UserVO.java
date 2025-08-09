package com.iss.hdbPilot.model.vo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;

    private String username;

    private String email;

    private String nickname;

    private String bio;

    private String avatarUrl;

    private LocalDateTime createdAt;
}
