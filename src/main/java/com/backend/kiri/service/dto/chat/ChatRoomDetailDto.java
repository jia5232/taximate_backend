package com.backend.kiri.service.dto.chat;

import com.backend.kiri.service.dto.post.PostDetailDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class ChatRoomDetailDto {
    private Long chatRoomId;
    private PostDetailDto postDetailDto;
    private MessageDetailDto lastMessageDetail;
}
