package com.backend.kiri.service.dto.post;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
public class PostDetailDto extends PostFormDto {
    private Long id;
    private Boolean isAuthor;
    private String openChatLink;
    private String authorName;
}