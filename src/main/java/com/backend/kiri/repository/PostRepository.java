package com.backend.kiri.repository;

import com.backend.kiri.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 커서 위치(lastPostId) 이후의 Post를 pageSize 만큼 가져옴
    // OrderBy 쿼리는 추후 수정 필요 (departTime 기준 최신순 정렬)
    @Query("SELECT p FROM Post p WHERE p.id > :lastPostId AND p.isFromSchool = :isFromSchool" +
            " AND (p.depart LIKE %:searchKeyword% OR p.arrive LIKE %:searchKeyword%)" +
            " ORDER BY p.id ASC") //현재시각 기준 departTime이 이후인 애들만!
    List<Post> findFilteredPosts(@Param("lastPostId") Long lastPostId, @Param("isFromSchool") boolean isFromSchool, @Param("searchKeyword") String searchKeyword, Pageable pageable);
}