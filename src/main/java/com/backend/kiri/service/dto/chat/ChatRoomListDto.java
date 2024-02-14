package com.backend.kiri.service.dto.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
public class ChatRoomListDto {
    private MetaData meta;
    private List<ChatRoomDetailDto> data;

    @ToString
    @Getter @Setter
    public static class MetaData {
        private Long count;
        private boolean hasMore;
    }
}