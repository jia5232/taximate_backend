package com.backend.kiri.service;

import com.backend.kiri.domain.*;
import com.backend.kiri.exception.exceptions.ChatRoomFullException;
import com.backend.kiri.exception.exceptions.NotFoundChatRoomException;
import com.backend.kiri.exception.exceptions.NotFoundMemberException;
import com.backend.kiri.exception.exceptions.NotFoundPostException;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.*;
import com.backend.kiri.service.dto.chat.ChatRoomDto;
import com.backend.kiri.service.dto.chat.ChatRoomListDto;
import com.backend.kiri.service.dto.chat.ChatRoomMapper;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class ChatRoomService {
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final PostRepository postRepository;
    private final MemberPostRepository memberPostRepository;
    private final JWTUtil jwtUtil;
    private final SimpMessagingTemplate messagingTemplate;

    //채팅방 참여 여부 확인
    public boolean isMemberJoinedChatRoom(Long postId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException("Not Found Post"));

        return post.getMemberPosts().stream()
                .anyMatch(memberPost -> memberPost.getMember().equals(member));
    }

    public Long joinChatRoom(Long postId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundPostException("Not Found Post"));

        ChatRoom chatRoom = chatRoomRepository.findByPostId(postId)
                .orElseThrow(() -> new NotFoundChatRoomException("Not Found ChatRoom"));

        if(post.getNowMember() >= post.getMaxMember()){
            throw new ChatRoomFullException("인원 초과입니다.");
        }
        post.addMember(member, false);
        post.setNowMember(post.getNowMember() + 1); // 현재 인원수 증가
        // 트랜잭션 커밋 시, 여기서 변경 감지가 일어나 변경된 사항이 데이터베이스에 반영됨.

        // 입장 알림 메시지
        Message message = new Message();
        message.setType(MessageType.ENTER);
        message.setSender(member);
        message.setChatRoom(chatRoom);
        message.setContent(member.getNickname() + "님이 채팅방에 입장하셨습니다.");
        message.setCreatedTime(LocalDateTime.now());
        messageRepository.save(message);

        MessageResponseDto messageResponseDto = convertToMessageResponseDto(message);

        messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoom.getId(), messageResponseDto);

        return post.getChatRoom().getId();
    }

    public Long leaveChatRoom(Long chatRoomId, String accessToken){
        String email = jwtUtil.getUsername(accessToken);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundChatRoomException("Not Found ChatRoom"));

        MemberPost memberPost = member.getMemberPosts().stream()
                .filter(mp -> mp.getPost().getChatRoom().equals(chatRoom))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("채팅방의 멤버가 아닙니다."));

        // 퇴장 알림 메시지
        Message message = new Message();
        message.setType(MessageType.LEAVE);
        message.setSender(member);
        message.setChatRoom(chatRoom);
        message.setContent(member.getNickname() + "님이 채팅방을 나가셨습니다.");
        message.setCreatedTime(LocalDateTime.now());
        messageRepository.save(message);

        MessageResponseDto messageResponseDto = convertToMessageResponseDto(message);

        messagingTemplate.convertAndSend("/sub/chatroom/" + chatRoom.getId(), messageResponseDto);

        Post post = memberPost.getPost();
        post.removeMember(member);
        post.getMemberPosts().remove(memberPost);
        post.setNowMember(post.getNowMember() - 1); // 현재 인원수 감소
        postRepository.save(post);

        member.getMemberPosts().remove(memberPost);
        memberPostRepository.delete(memberPost);
        memberRepository.save(member);

        return chatRoom.getId();
    }

    // 해당 채팅방의 아직 안읽은 메시지 보여주는 기능!
    // 사용자가 채팅방에 입장할 때, 나갈 때 해당 사용자의 MemberPost의 lastReadAt값을 현재 시간으로 설정.
    public void updateLastReadAt(Long chatRoomId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("멤버를 찾을 수 없습니다."));

        MemberPost memberPost = member.getMemberPosts().stream()
                .filter(mp -> mp.getPost().getChatRoom().getId().equals(chatRoomId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("현재 채팅방의 멤버가 아닙니다."));

        memberPost.setLastReadAt(LocalDateTime.now());
        memberPostRepository.save(memberPost); // 변경사항을 저장
    }


    @Transactional(readOnly = true)
    public ChatRoomListDto getChatRoomsForMemberWithLastMessage(Long lastPostId, int pageSize, String accessToken) {
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id").ascending());

        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("Not Found Member"));

        Page<ChatRoom> chatRoomPage = chatRoomRepository.findChatRoomsByMemberAfterLastId(member.getId(), lastPostId, pageable);

        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();

        for (ChatRoom chatRoom : chatRoomPage) {
            Post post = chatRoom.getPost();
            MemberPost memberPost = memberPostRepository.findByMemberAndPost(member, post)
                    .orElseThrow(() -> new IllegalStateException("Member, Post에 대한 MemberPost를 찾을 수 없습니다."));

            int unreadMessageCount = messageRepository.countByChatRoomAndTypeAndCreatedTimeAfter(chatRoom, MessageType.COMMON, memberPost.getLastReadAt());

            Message lastMessage = messageRepository.findFirstByChatRoomOrderByCreatedTimeDesc(chatRoom, PageRequest.of(0, 1)).stream().findFirst().orElse(null);

            ChatRoomDto chatRoomDto = ChatRoomMapper.toChatRoomDto(chatRoom, member, memberPost, lastMessage, unreadMessageCount);
            chatRoomDtos.add(chatRoomDto);
        }

        chatRoomDtos.sort(Comparator.comparing(ChatRoomDto::getMessageCreatedTime, Comparator.nullsLast(Comparator.reverseOrder())));

        ChatRoomListDto chatRoomListDto = new ChatRoomListDto();
        ChatRoomListDto.MetaData metaData = new ChatRoomListDto.MetaData();
        metaData.setCount(chatRoomDtos.size());
        metaData.setHasMore(!chatRoomPage.isLast());
        chatRoomListDto.setMeta(metaData);
        chatRoomListDto.setData(chatRoomDtos);

        return chatRoomListDto;
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
