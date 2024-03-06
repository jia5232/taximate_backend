package com.backend.kiri.controller;

import com.backend.kiri.service.ChatRoomService;
import com.backend.kiri.service.dto.chat.ChatRoomListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/chatrooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/is-joined/{postId}")
    public ResponseEntity<Boolean> isMemberJoinedChatRoom(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authorization
    ){
        String accessToken = authorization.split(" ")[1];
        return ResponseEntity.ok(chatRoomService.isMemberJoinedChatRoom(postId, accessToken));
    }

    @PostMapping("/join/{postId}")
    public ResponseEntity<Long> joinChatRoom(
            @PathVariable Long postId,
            @RequestHeader("Authorization") String authorization
    ){
        String accessToken = authorization.split(" ")[1];
        Long chatRoomId = chatRoomService.joinChatRoom(postId, accessToken);
        return ResponseEntity.ok(chatRoomId);
    }

    @DeleteMapping("/leave/{chatRoomId}")
    public ResponseEntity leaveChatRoom(
            @PathVariable Long chatRoomId,
            @RequestHeader("Authorization") String authorization
    ){
        String accessToken = authorization.split(" ")[1];
        Long id = chatRoomService.leaveChatRoom(chatRoomId, accessToken);
        return ResponseEntity.ok(id);
    }

    // 해당 채팅방에서 마지막에 존재한 시간을 업데이트해주는 api
    // 사용자가 채팅방에 입장할 때, 나갈 때 해당 사용자의 MemberPost의 lastReadAt값을 현재 시간으로 설정.
    @PutMapping("/update-last-read/{chatRoomId}")
    public ResponseEntity<Void> updateLastRead(@PathVariable Long chatRoomId,
                                               @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        chatRoomService.updateLastReadAt(chatRoomId, accessToken);
        return ResponseEntity.ok().build(); // 200 OK 응답을 반환
    }

    // 사용자가 포함된 모든 채팅방을 조회하는 api
    @GetMapping("/my")
    public ResponseEntity<ChatRoomListDto> getChatRoomsForMember(
            @RequestParam(required = false, defaultValue = "0") Long lastPostId,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader("Authorization") String authorization
            ) {
        String accessToken = authorization.split(" ")[1];
        ChatRoomListDto chatRoomListDto = chatRoomService.getChatRoomsForMemberWithLastMessage(lastPostId, pageSize, accessToken);
        return ResponseEntity.ok(chatRoomListDto);
    }
}
