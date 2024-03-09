package com.backend.kiri.service;

import com.backend.kiri.domain.*;
import com.backend.kiri.exception.exceptions.NotFoundChatRoomException;
import com.backend.kiri.exception.exceptions.NotFoundMemberException;
import com.backend.kiri.repository.ChatRoomRepository;
import com.backend.kiri.repository.MemberRepository;
import com.backend.kiri.repository.MessageRepository;
import com.backend.kiri.service.dto.chat.MessageListDto;
import com.backend.kiri.service.dto.chat.MessageRequestDto;
import com.backend.kiri.service.dto.chat.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 채팅 내역 조회
    @Transactional(readOnly = true)
    public MessageListDto getChatHistory(Long chatRoomId, Long lastMessageId, int pageSize) {
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id").descending());
        Page<Message> messagesPage;

        if (lastMessageId == 0) {
            messagesPage = messageRepository.findByChatRoomIdOrderByIdDesc(chatRoomId, pageable);
        } else {
            messagesPage = messageRepository.findByChatRoomIdAndIdLessThanOrderByIdDesc(chatRoomId, lastMessageId, pageable);
        }

        List<MessageResponseDto> messageResponseDtos = messagesPage.getContent().stream()
                .map(message -> convertToMessageResponseDto(message))
                .collect(Collectors.toList());

        MessageListDto messageListDto = new MessageListDto();
        MessageListDto.MetaData metaData = new MessageListDto.MetaData();
        metaData.setCount(messageResponseDtos.size());
        metaData.setHasMore(!messagesPage.isLast());
        messageListDto.setMeta(metaData);
        messageListDto.setData(messageResponseDtos);

        return messageListDto;
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
