package com.backend.kiri.controller;

import com.backend.kiri.service.ChatService;
import com.backend.kiri.service.dto.chat.MessageRequestDto;
import com.backend.kiri.service.dto.chat.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public ResponseEntity<MessageResponseDto> sendMessage(@Payload MessageRequestDto messageRequestDto, Principal principal) {
        System.out.println("chatController.sendMessage()");
        if(principal==null){
            System.out.println("Principal is null");
        }
        String email = principal.getName();
        System.out.println("principal: email is"+email);
        MessageResponseDto messageResponseDto = chatService.sendMessage(messageRequestDto, email);
        return ResponseEntity.ok(messageResponseDto);
    }
}
