package com.backend.kiri.service.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class JoinDto {
    private String email; //이메일
    private String password;
    private String nickname;
    private String univName;
    private Boolean isAccept;
    private Boolean isEmailAuthenticated;
}
