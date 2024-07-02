package com.backend.taximate.service.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class JoinDto { //회원가입용 DTO
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String nickname;
    @NotBlank
    private String univName;
    @NotNull
    private Boolean isAccept;
    @NotNull
    private Boolean isEmailAuthenticated;
}
