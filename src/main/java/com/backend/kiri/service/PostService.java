package com.backend.kiri.service;

import com.backend.kiri.domain.ChatRoom;
import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.MemberPost;
import com.backend.kiri.domain.Post;
import com.backend.kiri.exception.NotFoundChatRoomException;
import com.backend.kiri.exception.NotFoundMemberException;
import com.backend.kiri.exception.NotFoundPostException;
import com.backend.kiri.exception.UnauthorizedAccessException;
import com.backend.kiri.jwt.JWTUtil;
import com.backend.kiri.repository.MemberRepository;
import com.backend.kiri.repository.PostRepository;
import com.backend.kiri.service.dto.post.PostDetailDto;
import com.backend.kiri.service.dto.post.PostFormDto;
import com.backend.kiri.service.dto.post.PostListDto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class PostService {
    final PostRepository postRepository;
    final MemberRepository memberRepository;
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

        // 채팅방 생성 및 연관관계 설정.
        ChatRoom chatRoom = new ChatRoom();
        post.setChatRoom(chatRoom); // post의 연관관계 메서드 사용


        // MemberPost생성을 위한 작업
        // 글 작성자는 글을 작성할 때 이걸 통해서 joinChatRoom() 처리가 됨!
        post.addMember(member, true);

        postRepository.save(post);

        return post.getId();
    }

    public PostDetailDto detailPost(Long postId, String accessToken){
        String email = jwtUtil.getUsername(accessToken);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundPostException("PostService.detailPost: NotFoundPost"));
        return convertToDetailDto(post, email);
    }

    public Long updatePost(Long postId, PostFormDto postFormDto, String accessToken) {
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("PostService.updatePost: NotFoundMember"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundPostException("PostService.updatePost: NotFoundPost"));

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

        postRepository.save(post);

        return post.getId();
    }

    private LocalDateTime getFormattedDepartTime(PostFormDto postFormDto) {
        String departTimeString = postFormDto.getDepartTime();
        LocalDateTime departTime = LocalDateTime.parse(departTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime seoulZoneDepartTime = departTime.atZone(seoulZoneId);
        LocalDateTime realDepartTime = seoulZoneDepartTime.toLocalDateTime();
        return realDepartTime;
    }

    public void deletePost(Long postId, String accessToken){
        String email = jwtUtil.getUsername(accessToken);
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundMemberException("Not Found Member"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundPostException("No tFound Post"));

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
            throw new IllegalStateException("참여자가 있어 삭제가 불가합니다.");
        }

        postRepository.delete(post);
    }

    public PostListDto getFilteredPosts(Pageable pageable, Long lastPostId, Boolean isFromSchool, String searchKeyword, String accessToken){
        String email = jwtUtil.getUsername(accessToken);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundMemberException("Not Found Member"));


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

    public PostDetailDto getPostInfoByChatRoomId(Long chatRoomId, String accessToken){
        String email = jwtUtil.getUsername(accessToken);
        Post post = postRepository.findByChatRoom_Id(chatRoomId).orElseThrow(() -> new NotFoundChatRoomException("Not Found ChatRoom"));
        return convertToDetailDto(post, email);
    }

    private static PostDetailDto convertToDetailDto(Post findPost, String email) {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(findPost.getId());
        postDetailDto.setChatRoomId(findPost.getChatRoom().getId());
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

    // 마이페이지 내가 쓴 글 조회용
    private static PostDetailDto convertToMyPageDetailDto(Post findPost) {
        PostDetailDto postDetailDto = new PostDetailDto();
        postDetailDto.setId(findPost.getId());
        postDetailDto.setChatRoomId(findPost.getChatRoom().getId());
        postDetailDto.setIsFromSchool(findPost.isFromSchool());
        postDetailDto.setDepart(findPost.getDepart());
        postDetailDto.setArrive(findPost.getArrive());
        postDetailDto.setDepartTime(findPost.getDepartTime().toString());
        postDetailDto.setCost(findPost.getCost());
        postDetailDto.setMaxMember(findPost.getMaxMember());
        postDetailDto.setNowMember(findPost.getNowMember());
        postDetailDto.setIsAuthor(true);

        return postDetailDto;
    }
}
