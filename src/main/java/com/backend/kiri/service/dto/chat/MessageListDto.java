package com.backend.kiri.service.dto.chat;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter @Setter
public class MessageListDto {
    private MetaData meta;
    private List<MessageResponseDto> data;

    @ToString
    @Getter @Setter
    public static class MetaData {
        private int count;
        private boolean hasMore;
    }
}