package com.backend.kiri.service.dto.post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@Getter @Setter
public class PostFormDto {
    private Boolean isFromSchool;
    private String depart;
    private String arrive;
    private String departTime;
    private int cost;
    private int maxMember;
    private int nowMember;
}