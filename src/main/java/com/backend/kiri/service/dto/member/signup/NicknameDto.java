package com.backend.kiri.service.dto.member.signup;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class NicknameDto {
    @NotBlank
    private String nickname;
}
