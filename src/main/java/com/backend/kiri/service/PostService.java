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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import jakarta.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public PostListDto getFilteredPosts(Pageable pageable, Long lastPostId, Boolean isFromSchool, String searchKeyword, String accessToken){
        String email = jwtUtil.getUsername(accessToken);

        Specification<Post> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(lastPostId != null){
                predicates.add(criteriaBuilder.greaterThan(root.get("id"), lastPostId));
            }

            if(isFromSchool != null){
                predicates.add(criteriaBuilder.equal(root.get("isFromSchool"), isFromSchool));
            }

            if(searchKeyword != null && !searchKeyword.isEmpty()){
                Predicate departPredicate = criteriaBuilder.like(root.get("depart"), "%" + searchKeyword + "%");
                Predicate arrivePredicate = criteriaBuilder.like(root.get("arrive"), "%" + searchKeyword + "%");
                predicates.add(criteriaBuilder.or(departPredicate, arrivePredicate));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Post> page = postRepository.findAll(spec, pageable);
        List<Post> posts = page.getContent();

        List<PostDetailDto> postDetailDtos = posts.stream()
                .map((p) -> convertToDetailDto(p, email)).collect(Collectors.toList());

        PostListDto postListDto = new PostListDto();
        postListDto.setData(postDetailDtos);

        PostListDto.MetaData metaData = new PostListDto.MetaData();
        metaData.setCount(postDetailDtos.size());
        metaData.setHasMore(!page.isLast());
        //Page 객체가 제공하는 isLast() : 현재페이지가 마지막일 경우 true를 리턴. 따라서 hasMore는 !page.isLast()가 된다.
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
