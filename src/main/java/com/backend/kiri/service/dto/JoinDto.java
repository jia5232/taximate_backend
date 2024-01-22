package com.backend.kiri.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class JoinDto {
    private String username; //이메일
    private String password;
    private String nickname;
    private String univName;
    private Boolean isAccept;
    private Boolean isEmailAuthenticated;
}
