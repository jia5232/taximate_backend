package com.backend.kiri.controller;

import com.backend.kiri.service.ChatService;
import com.backend.kiri.service.dto.chat.MessageListDto;
import com.backend.kiri.service.dto.chat.MessageRequestDto;
import com.backend.kiri.service.dto.chat.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

    // 이전의 채팅 내역 조회 -> 채팅방에 입장할 때마다 이 api가 호출된다.
    @GetMapping("/history/{chatRoomId}")
    @ResponseBody
    public ResponseEntity<MessageListDto> getChatHistory(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false, defaultValue = "0") Long lastMessageId,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        MessageListDto messageListDto = chatService.getChatHistory(chatRoomId, lastMessageId, pageSize);
        return ResponseEntity.ok(messageListDto);
    }

    @MessageMapping("/chat/message")
    public ResponseEntity<MessageResponseDto> sendMessage(@Payload MessageRequestDto messageRequestDto, Principal principal) {
        System.out.println("chatController.sendMessage()");
        if (principal == null) {
            System.out.println("Principal is null");
        }
        String email = principal.getName();
        System.out.println("principal: email is" + email);
        MessageResponseDto messageResponseDto = chatService.sendMessage(messageRequestDto, email);
        return ResponseEntity.ok(messageResponseDto);
    }
}
