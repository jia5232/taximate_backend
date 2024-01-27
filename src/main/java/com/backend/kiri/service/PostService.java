package com.backend.kiri.service;

import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.Post;
import com.backend.kiri.exception.NotFoundMemberException;
import com.backend.kiri.exception.NotFoundPostException;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.MemberRepository;
import com.backend.kiri.repository.PostRepository;
import com.backend.kiri.service.dto.post.PostDetailDto;
import com.backend.kiri.service.dto.post.PostFormDto;
import com.backend.kiri.service.dto.post.PostListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    final PostRepository postRepository;
    final MemberRepository memberRepository;
    private final JWTUtil jwtUtil;

    public Long createPost(PostFormDto postFormDto, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Optional<Member> member = memberRepository.findByEmail(email);
        if(member==null){
            throw new NotFoundMemberException("PostService.createPost: NotFoundMember");
        }

        boolean isFromSchool = postFormDto.getIsFromSchool();
        Post post = new Post();
        post.setFromSchool(isFromSchool);

        String depart = isFromSchool ? member.get().getUnivName() : postFormDto.getDepart();
        post.setDepart(depart);

        String arrive = isFromSchool ? postFormDto.getArrive() : member.get().getUnivName();
        post.setArrive(arrive);

        // 플러터에서 '2024-01-26T13:17:00.000' 형식으로 들어온 스트링을 LocalDateTime format으로 변경
        String departTimeString = postFormDto.getDepartTime();
        LocalDateTime departTime = LocalDateTime.parse(departTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime seoulZoneDepartTime = departTime.atZone(seoulZoneId);
        LocalDateTime realDepartTime = seoulZoneDepartTime.toLocalDateTime();

        post.setDepartTime(realDepartTime);
        post.setCreatedTime(LocalDateTime.now());
        post.setCost(postFormDto.getCost());
        post.setMaxMember(postFormDto.getMaxMember());
        post.setNowMember(postFormDto.getNowMember());

        //추후 채팅방 관련 작업 필요

        //MemberPost생성을 위한 작업
        post.addMember(member.get(), true);

        postRepository.save(post);

        return post.getId();
    }

    public PostDetailDto detailPost(Long postId, String accessToken){
        String email = jwtUtil.getUsername(accessToken);

        Optional<Post> post = postRepository.findById(postId);
        if(post==null){
            throw new NotFoundPostException("PostService.detailPost: NotFoundPost");
        }
        Post findPost = post.get();
        return convertToDetailDto(findPost, email);
    }

    public PostListDto getPosts(Long lastPostId, int pageSize, String accessToken){
        String email = jwtUtil.getUsername(accessToken);

        Pageable pageable = PageRequest.of(0, pageSize);
        List<Post> posts = postRepository.findPostsAfterCursor(lastPostId, pageable);

        PostListDto postListDto = new PostListDto();

        List<PostDetailDto> postDetailDtos = posts.stream()
                .map((p) -> convertToDetailDto(p, email)).collect(Collectors.toList());
        postListDto.setData(postDetailDtos);

        PostListDto.MetaData metaData = new PostListDto.MetaData();
        metaData.setCount(postDetailDtos.size());
        metaData.setHasMore(postDetailDtos.size()==pageSize); //가져오고싶은 만큼 다 가져왔으면 데이터가 더 있다는거니까!
        postListDto.setMeta(metaData);

        return postListDto;
    }

    private static PostDetailDto convertToDetailDto(Post findPost, String email) {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(findPost.getId());
        postDetailDto.setIsFromSchool(findPost.isFromSchool());
        postDetailDto.setDepart(findPost.getDepart());
        postDetailDto.setArrive(findPost.getArrive());
        postDetailDto.setDepartTime(findPost.getDepartTime().toString());
        postDetailDto.setCost(findPost.getCost());
        postDetailDto.setMaxMember(findPost.getMaxMember());
        postDetailDto.setNowMember(findPost.getNowMember());

        boolean isAuthor = findPost.getMemberPosts().stream()
                .anyMatch(mp -> mp.getMember().getEmail().equals(email) && Boolean.TRUE.equals(mp.getIsAuthor()));
        postDetailDto.setIsAuthor(isAuthor);

        return postDetailDto;
    }
}
