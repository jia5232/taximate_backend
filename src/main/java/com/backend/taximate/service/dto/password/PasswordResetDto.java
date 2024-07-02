package com.backend.taximate.service.dto.password;

import lombok.Data;

@Data
public class PasswordResetDto {
    private String email;
    private String authNumber;
    private String newPassword;
}

