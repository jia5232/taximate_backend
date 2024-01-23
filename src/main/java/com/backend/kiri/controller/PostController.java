package com.backend.kiri.controller;

import com.backend.kiri.service.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController { //더미
    @GetMapping("/posts")
    public List<PostDto> posts(){
        List<PostDto> posts = new ArrayList<>();
        posts.add(new PostDto(1L, true, "국민대","보문역", LocalDateTime.now().plusHours(2), 10000,3,1));
        posts.add(new PostDto(2L, true, "국민대","성신여대입구역", LocalDateTime.now().plusHours(2), 10000,3,1));
        posts.add(new PostDto(3L, true, "국민대","길음역", LocalDateTime.now().plusHours(2), 10000,3,1));
        posts.add(new PostDto(3L, true, "국민대","보문역", LocalDateTime.now().plusHours(2), 10000,3,1));
        posts.add(new PostDto(3L, true, "국민대","보문역", LocalDateTime.now().plusHours(2), 10000,3,1));
        return posts;

    }
}
