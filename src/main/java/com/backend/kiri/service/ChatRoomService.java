package com.backend.kiri.service;

import com.backend.kiri.domain.*;
import com.backend.kiri.exception.ChatRoomFullException;
import com.backend.kiri.exception.NotFoundChatRoomException;
import com.backend.kiri.exception.NotFoundMemberException;
import com.backend.kiri.exception.NotFoundPostException;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.*;
import com.backend.kiri.service.dto.chat.ChatRoomDto;
import com.backend.kiri.service.dto.chat.ChatRoomListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (post.getChatRoom() == null) {
            throw new IllegalStateException("채팅방이 없습니다.");
        }

        if(post.getNowMember() >= post.getMaxMember()){
            throw new ChatRoomFullException("인원 초과입니다.");
        }

        post.addMember(member, false);
        post.setNowMember(post.getNowMember() + 1); // 현재 인원수 증가
        //트랜잭션 커밋 시, 여기서 변경 감지가 일어나 변경된 사항이 데이터베이스에 반영됨.

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

    @Transactional(readOnly = true)
    public ChatRoomListDto getChatRoomsForMemberWithLastMessage(Long lastPostId, int pageSize, String accessToken) {
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by("id").ascending());

        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("NotFoundMember"));

        Page<ChatRoom> chatRoomPage = chatRoomRepository.findChatRoomsByMemberAfterLastId(member.getId(), lastPostId, pageable);
        List<ChatRoomDto> chatRoomDtos = chatRoomPage.getContent().stream()
                .map(chatRoom -> {
                    Post post = chatRoom.getPost();
                    ChatRoomDto chatRoomDto = new ChatRoomDto();
                    chatRoomDto.setChatRoomId(chatRoom.getId());
                    chatRoomDto.setDepart(post.getDepart());
                    chatRoomDto.setArrive(post.getArrive());
                    chatRoomDto.setDepartTime(post.getDepartTime());
                    chatRoomDto.setNowMember(post.getNowMember());

                    Message lastMessage = messageRepository.findFirstByChatRoomCustom(chatRoom, PageRequest.of(0, 1)).stream().findFirst().orElse(null);
                    if(lastMessage != null){
                        chatRoomDto.setLastMessageContent(lastMessage.getContent());
                        chatRoomDto.setMessageCreatedTime(lastMessage.getCreatedTime());
                    }
                    return chatRoomDto;
                }).collect(Collectors.toList());

        ChatRoomListDto chatRoomListDto = new ChatRoomListDto();
        ChatRoomListDto.MetaData metaData = new ChatRoomListDto.MetaData();
        metaData.setCount(chatRoomDtos.size());
        metaData.setHasMore(!chatRoomPage.isLast());
        chatRoomListDto.setMeta(metaData);
        chatRoomListDto.setData(chatRoomDtos);

        return chatRoomListDto;
    }
}
