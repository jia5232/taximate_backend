package com.backend.taximate.service.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MemberDto { //멤버 조회용 DTO
    private Long id;
    private String email;
    private String nickname;
    private String univName;
}
