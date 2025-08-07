package com.iss.hdbPilot.model.dto;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private Long id;
    private String username;
    private String email;
    private String nickname;

}
