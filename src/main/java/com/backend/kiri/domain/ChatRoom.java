package com.backend.kiri.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class ChatRoom {
    @Id @GeneratedValue
    @Column(name = "chatRoom_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Post post;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();
}
