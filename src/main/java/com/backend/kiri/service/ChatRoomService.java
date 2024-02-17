package com.backend.kiri.service;

import com.backend.kiri.domain.*;
import com.backend.kiri.exception.ChatRoomFullException;
import com.backend.kiri.exception.NotFoundChatRoomException;
import com.backend.kiri.exception.NotFoundMemberException;
import com.backend.kiri.exception.NotFoundPostException;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.*;
import com.backend.kiri.service.dto.chat.ChatRoomDetailDto;
import com.backend.kiri.service.dto.chat.ChatRoomListDto;
import com.backend.kiri.service.dto.chat.MessageDetailDto;
import com.backend.kiri.service.dto.post.PostDetailDto;
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
        List<ChatRoomDetailDto> chatRoomDetailDtos = chatRoomPage.getContent().stream()
                .map(chatRoom -> {
                    Pageable firstMessagePageable = PageRequest.of(0, 1);
                    List<Message> messages = messageRepository.findFirstByChatRoomCustom(chatRoom, firstMessagePageable);
                    Message lastMessage = messages.isEmpty() ? null : messages.get(0);
                    return convertToChatRoomDetailDto(chatRoom, lastMessage, email);
                })
                .collect(Collectors.toList());

        return convertToChatRoomListDto(chatRoomDetailDtos, chatRoomPage.isLast());
    }

    private MessageDetailDto convertToMessageDetailDto(Message message) {
        MessageDetailDto dto = new MessageDetailDto();
        dto.setContent(message.getContent());
        dto.setCreatedTime(message.getCreatedTime());
        return dto;
    }

    private ChatRoomDetailDto convertToChatRoomDetailDto(ChatRoom chatRoom, Message lastMessage, String email) {
        ChatRoomDetailDto dto = new ChatRoomDetailDto();
        dto.setChatRoomId(chatRoom.getId());
        dto.setPostDetailDto(convertToPostDetailDto(chatRoom.getPost(), email));
        if (lastMessage != null) {
            dto.setLastMessageDetail(convertToMessageDetailDto(lastMessage));
        } else {
            dto.setLastMessageDetail(null);
        }
        return dto;
    }

    private ChatRoomListDto convertToChatRoomListDto(List<ChatRoomDetailDto> chatRoomDetails, boolean hasMore) {
        ChatRoomListDto listDto = new ChatRoomListDto();
        ChatRoomListDto.MetaData metaData = new ChatRoomListDto.MetaData();
        metaData.setCount((long) chatRoomDetails.size());
        metaData.setHasMore(hasMore);
        listDto.setMeta(metaData);
        listDto.setData(chatRoomDetails);
        return listDto;
    }

    private static PostDetailDto convertToPostDetailDto(Post post, String email) {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(post.getId());
        postDetailDto.setIsFromSchool(post.isFromSchool());
        postDetailDto.setDepart(post.getDepart());
        postDetailDto.setArrive(post.getArrive());
        postDetailDto.setDepartTime(post.getDepartTime().toString());
        postDetailDto.setCost(post.getCost());
        postDetailDto.setMaxMember(post.getMaxMember());
        postDetailDto.setNowMember(post.getNowMember());
        postDetailDto.setChatRoomId(post.getChatRoom().getId());

        boolean isAuthor = post.getMemberPosts().stream()
                .anyMatch(mp -> mp.getMember().getEmail().equals(email) && mp.getIsAuthor());
        postDetailDto.setIsAuthor(isAuthor);

        return postDetailDto;
    }
}
