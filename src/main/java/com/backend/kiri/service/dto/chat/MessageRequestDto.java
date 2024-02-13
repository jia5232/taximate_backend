package com.backend.kiri.service.dto.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MessageRequestDto { //클라이언트가 메시지를 보낼 때 사용하는 DTO
    private Long chatRoomId;
    private String content;
}
