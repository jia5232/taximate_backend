package com.backend.kiri.controller;

import com.backend.kiri.service.ChatService;
import com.backend.kiri.service.dto.chat.MessageRequestDto;
import com.backend.kiri.service.dto.chat.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final ChatService chatService;

    // 이전의 채팅 내역 조회
    @GetMapping("/history/{chatRoomId}")
    public ResponseEntity<List<MessageResponseDto>> getChatHistory(@PathVariable Long chatRoomId) {
        List<MessageResponseDto> chatHistory = chatService.getChatHistory(chatRoomId);
        return ResponseEntity.ok(chatHistory);
    }

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
