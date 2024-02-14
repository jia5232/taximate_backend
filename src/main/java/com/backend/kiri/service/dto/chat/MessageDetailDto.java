package com.backend.kiri.service.dto.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter @Setter
public class MessageDetailDto {
    private String content;
    private LocalDateTime createdTime;
}
