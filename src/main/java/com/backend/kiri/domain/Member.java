package com.backend.kiri.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String nickname;
    private String email;
    private String password;
    private String univName;
    private Boolean isDeleted = false;
    private LocalDateTime cancellationDate;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberPost> memberPosts = new ArrayList<>();

    public void delete() {
        this.isDeleted = true;
        this.nickname = "(알 수 없음)";
        this.cancellationDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Member{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", univName='" + univName + '\'' +
                ", isDeleted=" + isDeleted +
                ", cancellationDate=" + cancellationDate +
                '}';
    }
}
