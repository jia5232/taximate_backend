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
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Long> createPost(@RequestBody PostFormDto postFormDto, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        Long postId = postService.createPost(postFormDto, accessToken);
        return ResponseEntity.ok(postId);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailDto> detailPost(@PathVariable Long postId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        PostDetailDto postDetailDto = postService.detailPost(postId, accessToken);
        return ResponseEntity.ok(postDetailDto);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Long> updatePost(@PathVariable Long postId, @RequestBody PostFormDto postFormDto, @RequestHeader("Authorization") String authorization){
        String accessToken = authorization.split(" ")[1];
        Long id = postService.updatePost(postId, postFormDto, accessToken);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Long> deletePost(@PathVariable Long postId, @RequestHeader("Authorization") String authorization){
        String accessToken = authorization.split(" ")[1];
        postService.deletePost(postId, accessToken);
        return ResponseEntity.ok(postId);
    }

    @PostMapping("/join/{postId}")
    public ResponseEntity<Void> joinPost(@PathVariable Long postId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        postService.joinPost(postId, accessToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/leave/{postId}")
    public ResponseEntity<Void> leavePost(@PathVariable Long postId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        postService.leavePost(postId, accessToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/is-joined/{postId}")
    public ResponseEntity<Boolean> isJoined(@PathVariable Long postId, @RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.split(" ")[1];
        boolean isJoined = postService.isMemberJoined(postId, accessToken);
        return ResponseEntity.ok(isJoined);
    }

    @GetMapping
    public ResponseEntity<PostListDto> getPosts(
            @RequestParam(required = false, defaultValue = "0") Long lastPostId,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false, defaultValue = "true") boolean isFromSchool,
            @RequestParam(required = false, defaultValue = "") String searchKeyword,
            @RequestHeader("Authorization") String authorization
    ) {
        String accessToken = authorization.split(" ")[1];
        Pageable pageable = PageRequest.of(0, pageSize);
        PostListDto postListDto = postService.getFilteredPosts(pageable, lastPostId, isFromSchool, searchKeyword, accessToken);
        return ResponseEntity.ok(postListDto);
    }

    @GetMapping("/myposts")
    public ResponseEntity<PostListDto> getMyPosts(
            @RequestParam(required = false, defaultValue = "0") Long lastPostId,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestHeader("Authorization") String authorization
    ) {
        String accessToken = authorization.split(" ")[1];
        Pageable pageable = PageRequest.of(0, pageSize);
        PostListDto postListDto = postService.getMyPosts(pageable, lastPostId, accessToken);
        return ResponseEntity.ok(postListDto);
    }
}
