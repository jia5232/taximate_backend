package com.backend.kiri.service;

import com.backend.kiri.domain.*;
import com.backend.kiri.exception.NotFoundChatRoomException;
import com.backend.kiri.exception.NotFoundMemberException;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.ChatRoomRepository;
import com.backend.kiri.repository.MemberPostRepository;
import com.backend.kiri.repository.MemberRepository;
import com.backend.kiri.repository.MessageRepository;
import com.backend.kiri.service.dto.chat.MessageRequestDto;
import com.backend.kiri.service.dto.chat.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final JWTUtil jwtUtil;

    // 채팅 내역 조회
    public List<MessageResponseDto> getChatHistory(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundChatRoomException("채팅방을 찾을 수 없습니다."));

        return messageRepository.findByChatRoom(chatRoom)
                .stream()
                .map(m -> convertToMessageResponseDto(m))
                .collect(Collectors.toList());
    }

    public MessageResponseDto sendMessage(MessageRequestDto messageRequestDto, String email) {
        Member sender = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(messageRequestDto.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatRoomException("채팅방을 찾을 수 없습니다."));

        Message message = new Message();
        message.setSender(sender);
        message.setType(MessageType.COMMON);
        message.setChatRoom(chatRoom);
        message.setContent(messageRequestDto.getContent());
        message.setCreatedTime(LocalDateTime.now());
        messageRepository.save(message);

        MessageResponseDto messageResponseDto = convertToMessageResponseDto(message);

        messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoom.getId(), messageResponseDto);
        return messageResponseDto;
    }

    private static MessageResponseDto convertToMessageResponseDto(Message message) {
        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setId(message.getId());
        messageResponseDto.setType(message.getType().toString());
        messageResponseDto.setContent(message.getContent());
        messageResponseDto.setNickname(message.getSender().getNickname());
        messageResponseDto.setCreatedTime(message.getCreatedTime());
        return messageResponseDto;
    }
}
