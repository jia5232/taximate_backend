package com.backend.taximate.service.dto.post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter @Setter
public class PostListDto {
    private MetaData meta;
    private List<PostDetailDto> data;

    @ToString
    @Getter @Setter
    public static class MetaData{
        private int count;
        private boolean hasMore;
    }
}
