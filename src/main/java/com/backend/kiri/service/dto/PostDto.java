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
    private int maxUser;
    private int currentUser;

    public PostDto(Long id, Boolean isFromSchool, String depart, String arrive, LocalDateTime departTime, int cost, int maxUser, int currentUser) {
        this.id = id;
        this.isFromSchool = isFromSchool;
        this.depart = depart;
        this.arrive = arrive;
        this.departTime = departTime;
        this.cost = cost;
        this.maxUser = maxUser;
        this.currentUser = currentUser;
    }
}