package com.backend.kiri.service.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class JoinDto { //회원가입용 DTO
    private String email;
    private String password;
    private String nickname;
    private String univName;
    private Boolean isAccept;
    private Boolean isEmailAuthenticated;
}
