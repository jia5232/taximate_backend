package com.backend.kiri.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Post {
    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;
    private boolean isFromSchool;
    private String depart;
    private String arrive;
    private LocalDateTime departTime;
    private LocalDateTime createdTime;
    private Integer cost;
    private Integer maxMember;
    private Integer nowMember;
    private Boolean isDeleted = false;  // 소프트 삭제 여부 추가
    private String openChatLink;  // 오픈채팅방 링크 추가

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 40)
    private List<MemberPost> memberPosts = new ArrayList<>();

    public void addMember(Member member, boolean isAuthor){
        MemberPost memberPost = new MemberPost(this, member, isAuthor);
        memberPosts.add(memberPost);
        member.getMemberPosts().add(memberPost);
    }

    public void removeMember(Member member){
        for (MemberPost memberPost : memberPosts) {
            if(memberPost.getPost().equals(this) && memberPost.getMember().equals(member)){
                memberPost.getMember().getMemberPosts().remove(memberPost);
                memberPost.setPost(null);
                memberPost.setMember(null);
            }
        }
    }

    // 글 삭제 처리 메서드
    public void delete() {
        this.isDeleted = true;
    }
}
