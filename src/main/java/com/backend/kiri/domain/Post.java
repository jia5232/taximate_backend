package com.backend.kiri.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Post {
    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;
    private boolean isFromSchool;
    private String depart;
    private String arrive;
    private LocalDateTime departTime;
    private LocalDateTime createdTime;
    private Integer cost;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;
}
