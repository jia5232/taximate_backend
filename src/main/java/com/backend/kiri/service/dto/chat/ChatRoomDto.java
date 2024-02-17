package com.backend.kiri.service.dto.chat;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ChatRoomDto {
    private Long chatRoomId;
    private String depart;
    private String arrive;
    private LocalDateTime departTime;
    private int nowMember;
    private String lastMessageContent;
    private LocalDateTime messageCreatedTime;
}
