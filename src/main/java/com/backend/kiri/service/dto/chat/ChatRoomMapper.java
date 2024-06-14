package com.backend.kiri.service.dto.chat;

import com.backend.kiri.domain.ChatRoom;
import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.MemberPost;
import com.backend.kiri.domain.Message;

public class ChatRoomMapper {
    public static ChatRoomDto toChatRoomDto(ChatRoom chatRoom, Member member, MemberPost memberPost, Message lastMessage, int unreadMessageCount) {
        ChatRoomDto chatRoomDto = new ChatRoomDto();
        chatRoomDto.setChatRoomId(chatRoom.getId());
        chatRoomDto.setUnreadMessageCount(unreadMessageCount);
        chatRoomDto.setDepart(chatRoom.getPost().getDepart());
        chatRoomDto.setArrive(chatRoom.getPost().getArrive());
        chatRoomDto.setDepartTime(chatRoom.getPost().getDepartTime());
        chatRoomDto.setNowMember(chatRoom.getPost().getNowMember());
        if (lastMessage != null) {
            chatRoomDto.setLastMessageContent(lastMessage.getContent());
            chatRoomDto.setMessageCreatedTime(lastMessage.getCreatedTime());
        }
        return chatRoomDto;
    }
}