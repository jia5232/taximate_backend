package com.backend.kiri.service;

import com.backend.kiri.domain.ChatRoom;
import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.Message;
import com.backend.kiri.exception.NotFoundChatRoomException;
import com.backend.kiri.exception.NotFoundMemberException;
import com.backend.kiri.repository.ChatRoomRepository;
import com.backend.kiri.repository.MemberRepository;
import com.backend.kiri.repository.MessageRepository;
import com.backend.kiri.service.dto.chat.MessageRequestDto;
import com.backend.kiri.service.dto.chat.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatService {
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageResponseDto sendMessage(MessageRequestDto messageRequestDto, String email) {
        Member sender = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("사용자를 찾을 수 없습니다."));
        ChatRoom chatRoom = chatRoomRepository.findById(messageRequestDto.getChatRoomId())
                .orElseThrow(() -> new NotFoundChatRoomException("채팅방을 찾을 수 없습니다."));

        Message message = new Message();
        message.setSender(sender);
        message.setChatRoom(chatRoom);
        message.setContent(messageRequestDto.getContent());
        message.setCreatedTime(LocalDateTime.now());
        messageRepository.save(message);

        MessageResponseDto messageResponseDto = new MessageResponseDto();
        messageResponseDto.setId(message.getId());
        messageResponseDto.setContent(message.getContent());
        messageResponseDto.setNickname(sender.getNickname());
        messageResponseDto.setCreatedTime(message.getCreatedTime());

        messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoom.getId(), messageResponseDto);
        return messageResponseDto;
    }
}
