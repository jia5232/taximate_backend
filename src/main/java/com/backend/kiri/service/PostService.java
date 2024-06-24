package com.backend.kiri.service;

import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.MemberPost;
import com.backend.kiri.domain.Post;
import com.backend.kiri.exception.exceptions.*;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.MemberPostRepository;
import com.backend.kiri.repository.MemberRepository;
import com.backend.kiri.repository.PostRepository;
import com.backend.kiri.service.dto.post.PostDetailDto;
import com.backend.kiri.service.dto.post.PostFormDto;
import com.backend.kiri.service.dto.post.PostListDto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class PostService {
    final PostRepository postRepository;
    final MemberRepository memberRepository;
    final MemberPostRepository memberPostRepository;
    private final JWTUtil jwtUtil;

    public Long createPost(PostFormDto postFormDto, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("PostService.createPost: NotFoundMember"));

        boolean isFromSchool = postFormDto.getIsFromSchool();
        Post post = new Post();
        post.setFromSchool(isFromSchool);

        String depart = isFromSchool ? member.getUnivName() : postFormDto.getDepart();
        post.setDepart(depart);

        String arrive = isFromSchool ? postFormDto.getArrive() : member.getUnivName();
        post.setArrive(arrive);

        // 플러터에서 '2024-01-26T13:17:00.000' 형식으로 들어온 스트링을 LocalDateTime format으로 변경
        LocalDateTime formattedDepartTime = getFormattedDepartTime(postFormDto);
        post.setDepartTime(formattedDepartTime);

        post.setCreatedTime(LocalDateTime.now());
        post.setCost(postFormDto.getCost());
        post.setMaxMember(postFormDto.getMaxMember());
        post.setNowMember(postFormDto.getNowMember());
        post.setOpenChatLink(postFormDto.getOpenChatLink());  // 오픈채팅방 링크 설정

        // MemberPost생성을 위한 작업
        post.addMember(member, true);

        postRepository.save(post);

        return post.getId();
    }

    public PostDetailDto detailPost(Long postId, String accessToken){
        String email = jwtUtil.getUsername(accessToken);
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(() -> new NotFoundPostException("PostService.detailPost: NotFoundPost"));
        return convertToDetailDto(post, email);
    }

    public Long updatePost(Long postId, PostFormDto postFormDto, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("PostService.updatePost: NotFoundMember"));
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(() -> new NotFoundPostException("PostService.updatePost: NotFoundPost"));

        boolean isFromSchool = postFormDto.getIsFromSchool();
        post.setFromSchool(isFromSchool);

        String depart = isFromSchool ? member.getUnivName() : postFormDto.getDepart();
        post.setDepart(depart);

        String arrive = isFromSchool ? postFormDto.getArrive() : member.getUnivName();
        post.setArrive(arrive);

        post.setCost(postFormDto.getCost());
        post.setMaxMember(postFormDto.getMaxMember());
        post.setNowMember(postFormDto.getNowMember());

        // 플러터에서 '2024-01-26T13:17:00.000' 형식으로 들어온 스트링을 LocalDateTime format으로 변경
        LocalDateTime formattedDepartTime = getFormattedDepartTime(postFormDto);
        post.setDepartTime(formattedDepartTime);
        post.setOpenChatLink(postFormDto.getOpenChatLink());  // 오픈채팅방 링크 설정

        postRepository.save(post);

        return post.getId();
    }

    public void deletePost(Long postId, String accessToken){
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("Not Found Member"));
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(() -> new NotFoundPostException("Not Found Post"));

        boolean isAuthor = post.getMemberPosts().stream()
                .anyMatch(memberPost -> memberPost.getMember().equals(member) && Boolean.TRUE.equals(memberPost.getIsAuthor()));

        if(!isAuthor){
            throw new UnauthorizedAccessException("글을 삭제할 수 있는 권한이 없습니다.");
        }

        // 작성자 외의 다른 참여자가 없는지 확인
        long participantsCount = post.getMemberPosts().stream()
                .filter(memberPost -> !memberPost.getMember().equals(member))
                .count();

        if (participantsCount > 0) {
            throw new NotEmptyPostException("참여자가 있어 삭제가 불가합니다.");
        }

        post.delete();  // 소프트 삭제 처리
        postRepository.save(post);
    }

    public void joinPost(Long postId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("Not Found Member"));
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(() -> new NotFoundPostException("Not Found Post"));

        if (post.getNowMember() >= post.getMaxMember()) {
            throw new ChatRoomFullException("인원 초과입니다.");
        }

        post.addMember(member, false);
        post.setNowMember(post.getNowMember() + 1); // 현재 인원수 증가
        postRepository.save(post);
    }

    public boolean isMemberJoined(Long postId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);

        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        if (!postOptional.isPresent() || !memberOptional.isPresent()) {
            return false;
        }

        Post post = postOptional.get();
        Member member = memberOptional.get();

        boolean isMemberJoined = post.getMemberPosts().stream()
                .anyMatch(mp -> mp.getMember().equals(member));

        return isMemberJoined;
    }

    public void leavePost(Long postId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("Not Found Member"));
        Post post = postRepository.findByIdAndIsDeletedFalse(postId).orElseThrow(() -> new NotFoundPostException("Not Found Post"));

        post.removeMember(member);
        post.setNowMember(post.getNowMember() - 1); // 현재 인원수 감소
        postRepository.save(post);
    }

    private LocalDateTime getFormattedDepartTime(PostFormDto postFormDto) {
        String departTimeString = postFormDto.getDepartTime();
        LocalDateTime departTime = LocalDateTime.parse(departTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime seoulZoneDepartTime = departTime.atZone(seoulZoneId);
        LocalDateTime realDepartTime = seoulZoneDepartTime.toLocalDateTime();
        return realDepartTime;
    }

    public PostListDto getFilteredPosts(Pageable pageable, Long lastPostId, Boolean isFromSchool, String searchKeyword, String accessToken){
        String email = jwtUtil.getUsername(accessToken);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));

        Specification<Post> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 출발 시간이 현재보다 지난 게시물은 필터링
            predicates.add(criteriaBuilder.greaterThan(root.get("departTime"), LocalDateTime.now()));

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

            // MemberPost와 Member로 조인하여 대학교 이름으로 필터링
            Subquery<MemberPost> memberPostSubquery = query.subquery(MemberPost.class);
            Root<MemberPost> memberPostRoot = memberPostSubquery.from(MemberPost.class);
            Join<MemberPost, Member> memberJoin = memberPostRoot.join("member");
            memberPostSubquery.select(memberPostRoot);
            memberPostSubquery.where(
                    criteriaBuilder.equal(memberJoin.get("univName"), member.getUnivName()),
                    criteriaBuilder.equal(memberPostRoot.get("post"), root) // MemberPost로 Post를 찾아서 비교
            );

            predicates.add(criteriaBuilder.exists(memberPostSubquery));

            // 소프트 삭제되지 않은 게시물만 필터링
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // 출발 시간이 임박한 순으로 정렬
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "departTime"));

        Page<Post> page = postRepository.findAll(spec, pageable);
        List<Post> posts = page.getContent();

        List<PostDetailDto> postDetailDtos = posts.stream()
                .map((p) -> convertToDetailDto(p, email)).collect(Collectors.toList());

        PostListDto postListDto = new PostListDto();
        postListDto.setData(postDetailDtos);

        PostListDto.MetaData metaData = new PostListDto.MetaData();
        metaData.setCount(postDetailDtos.size());
        metaData.setHasMore(!page.isLast());
        postListDto.setMeta(metaData);

        return postListDto;
    }

    public PostListDto getMyPosts(Pageable pageable, Long lastPostId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);

        // Specification을 사용하여 필터링 조건 정의
        Specification<Post> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // lastPostId가 있으면, 해당 ID 이후의 게시물만 조회
            if (lastPostId != 0) { // defaultValue가 "0"이므로, 0이 아닐 때만 조건 추가
                predicates.add(criteriaBuilder.greaterThan(root.get("id"), lastPostId));
            }

            // 게시물과 회원을 조인하고, 현재 사용자가 작성자인 게시물만 필터링
            Join<Post, MemberPost> memberPostJoin = root.join("memberPosts");
            predicates.add(criteriaBuilder.equal(memberPostJoin.get("member").get("email"), email));
            predicates.add(criteriaBuilder.isTrue(memberPostJoin.get("isAuthor"))); // 작성자인 경우만 필터링

            // 소프트 삭제되지 않은 게시물만 필터링
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Specification과 Pageable을 사용하여 게시물 조회
        Page<Post> page = postRepository.findAll(spec, pageable);
        List<Post> posts = page.getContent();

        // 조회된 Post 엔티티 목록을 PostDetailDto 목록으로 변환
        List<PostDetailDto> postDetailDtos = posts.stream()
                .map(p -> convertToMyPageDetailDto(p))
                .collect(Collectors.toList());

        PostListDto postListDto = new PostListDto();
        postListDto.setData(postDetailDtos);

        PostListDto.MetaData metaData = new PostListDto.MetaData();
        metaData.setCount(postDetailDtos.size());
        metaData.setHasMore(!page.isLast());
        postListDto.setMeta(metaData);

        return postListDto;
    }

    @Transactional(readOnly = true)
    public PostListDto getJoinedPosts(Pageable pageable, Long lastPostId, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);

        Specification<Post> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<Post, MemberPost> memberPostJoin = root.join("memberPosts");
            predicates.add(criteriaBuilder.equal(memberPostJoin.get("member").get("email"), email));
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));

            if (lastPostId != 0) {
                predicates.add(criteriaBuilder.greaterThan(root.get("id"), lastPostId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Post> page = postRepository.findAll(spec, pageable);
        List<PostDetailDto> postDetailDtos = page.getContent().stream()
                .map(post -> convertToDetailDto(post, email))
                .collect(Collectors.toList());

        PostListDto postListDto = new PostListDto();
        postListDto.setData(postDetailDtos);

        PostListDto.MetaData metaData = new PostListDto.MetaData();
        metaData.setCount(postDetailDtos.size());
        metaData.setHasMore(!page.isLast());
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
        postDetailDto.setOpenChatLink(findPost.getOpenChatLink());

        String authorName = null;
        boolean isAuthor = false;

        for (MemberPost mp : findPost.getMemberPosts()) {
            if (Boolean.TRUE.equals(mp.getIsAuthor())) {
                authorName = mp.getMember().getNickname();
            }
            if (mp.getMember().getEmail().equals(email) && Boolean.TRUE.equals(mp.getIsAuthor())) {
                isAuthor = true;
            }
            if (authorName != null && isAuthor) {
                break;
            }
        }

        postDetailDto.setIsAuthor(isAuthor);
        postDetailDto.setAuthorName(authorName);

        return postDetailDto;
    }


    // 마이페이지 내가 쓴 글 조회용
    private static PostDetailDto convertToMyPageDetailDto(Post findPost) {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(findPost.getId());
        postDetailDto.setIsFromSchool(findPost.isFromSchool());
        postDetailDto.setDepart(findPost.getDepart());
        postDetailDto.setArrive(findPost.getArrive());
        postDetailDto.setDepartTime(findPost.getDepartTime().toString());
        postDetailDto.setCost(findPost.getCost());
        postDetailDto.setMaxMember(findPost.getMaxMember());
        postDetailDto.setNowMember(findPost.getNowMember());
        postDetailDto.setIsAuthor(true);
        postDetailDto.setOpenChatLink(findPost.getOpenChatLink());

        return postDetailDto;
    }
}
