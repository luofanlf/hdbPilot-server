package com.iss.hdbPilot.model.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String newUsername;
    private String newEmail;
    private String newNickname;
    private String newBio;
    private String oldPassword;
    private String newPassword;
}