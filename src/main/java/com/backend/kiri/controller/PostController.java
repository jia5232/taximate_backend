package com.backend.kiri.controller;

import com.backend.kiri.service.PostService;
import com.backend.kiri.service.dto.post.PostDetailDto;
import com.backend.kiri.service.dto.post.PostFormDto;
import com.backend.kiri.service.dto.post.PostListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {
    final PostService postService;

    @PostMapping("/posts/create")
    public ResponseEntity createPost(@RequestBody PostFormDto postFormDto, @RequestHeader("Authorization") String authorization){
        String accessToken = authorization.split(" ")[1];
        Long postId = postService.createPost(postFormDto, accessToken);
        return ResponseEntity.ok(postId);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailDto> detailPost(@PathVariable Long postId, @RequestHeader("Authorization") String authorization){
        String accessToken = authorization.split(" ")[1];
        PostDetailDto postDetailDto = postService.detailPost(postId, accessToken);
        return ResponseEntity.ok(postDetailDto);
    }

    @GetMapping("/posts")
    public ResponseEntity<PostListDto> getPosts(
            @RequestParam(required = false, defaultValue = "0") Long lastPostId,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = "true") boolean isFromSchool,
            @RequestParam(required = false, defaultValue = "") String searchKeyword,
            @RequestHeader("Authorization") String authorization
            ){
        String accessToken = authorization.split(" ")[1];
        Pageable pageable = PageRequest.of(0, pageSize);
        PostListDto postListDto = postService.getFilteredPosts(pageable, lastPostId, isFromSchool, searchKeyword, accessToken);
        return ResponseEntity.ok(postListDto);
    }

    //deletePost 추후 작업 필요!
}
