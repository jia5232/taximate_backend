package com.backend.kiri.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
public class PostDto {
    private Long id;
    private Boolean isFromSchool;
    private String depart;
    private String arrive;
    private LocalDateTime departTime;
    private int cost;
    private int maxMember;
    private int nowMember;

    public PostDto(Long id, Boolean isFromSchool, String depart, String arrive, LocalDateTime departTime, int cost, int maxMember, int nowMember
    ) {
        this.id = id;
        this.isFromSchool = isFromSchool;
        this.depart = depart;
        this.arrive = arrive;
        this.departTime = departTime;
        this.cost = cost;
        this.maxMember = maxMember;
        this.nowMember = nowMember;
    }
}