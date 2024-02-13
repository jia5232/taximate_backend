package com.backend.kiri.service.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageResponseDto {
    private Long id;
    private String content;
    private String nickname;
    private LocalDateTime createdTime;
}
