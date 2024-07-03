package com.backend.taximate.service.dto.password;

import lombok.Data;

@Data
public class PasswordChangeDto {
    private String currentPassword;
    private String newPassword;
}
