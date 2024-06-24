package com.backend.kiri.repository;

import com.backend.kiri.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Optional<Post> findByIdAndIsDeletedFalse(Long postId);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    List<Post> findAllActive();

    @Query("SELECT p FROM Post p JOIN p.memberPosts mp WHERE mp.member.email = :email AND mp.post.isDeleted = false")
    Page<Post> findAllByMemberEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.memberPosts mp WHERE mp.member.email = :email AND mp.post.isDeleted = false")
    Page<Post> findAllByMemberAndIsDeletedFalse(@Param("email") String email, Pageable pageable);
}

