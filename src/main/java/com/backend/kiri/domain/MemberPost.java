package com.backend.kiri.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberPost {
    @Id @GeneratedValue
    @Column(name = "member_post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Boolean isAuthor;

    // 마지막으로 채팅방 메시지를 읽은 시간을 저장하기 위한 필드.
    private LocalDateTime lastReadAt;

    public MemberPost(Post post, Member member, boolean isAuthor, LocalDateTime lastReadAt) {
        this.post = post;
        this.member = member;
        this.isAuthor = isAuthor;
        this.lastReadAt = lastReadAt;
    }
}
