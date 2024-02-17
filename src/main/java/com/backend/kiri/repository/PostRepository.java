package com.backend.kiri.repository;

import com.backend.kiri.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    // 커서 위치(lastPostId) 이후의 Post를 pageSize 만큼 가져옴
//    @Query("SELECT p FROM Post p WHERE p.id > :lastPostId AND p.isFromSchool = :isFromSchool" +
//            " AND (p.depart LIKE %:searchKeyword% OR p.arrive LIKE %:searchKeyword%)" +
//            " ORDER BY p.id ASC") //현재시각 기준 departTime이 이후인 애들만!
//    List<Post> findFilteredPosts(@Param("lastPostId") Long lastPostId, @Param("isFromSchool") boolean isFromSchool, @Param("searchKeyword") String searchKeyword, Pageable pageable);


    Optional<Post> findByChatRoom_Id(Long chatRoomId);
}
