package com.backend.taximate.service.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.validator.constraints.Range;

@ToString
@Getter @Setter
public class PostFormDto {
    @NotNull
    private Boolean isFromSchool;
    @NotNull
    private String depart;
    @NotNull
    private String arrive;
    @NotBlank
    private String departTime;
    @Range(min = 4800, max = 500000)
    private int cost;
    @Range(min = 2, max = 4)
    private int maxMember;
    @NotBlank
    private int nowMember;
    @NotBlank
    private String openChatLink;  // 오픈채팅방 링크 추가
}
