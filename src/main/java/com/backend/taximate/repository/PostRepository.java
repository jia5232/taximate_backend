package com.backend.taximate.repository;

import com.backend.taximate.domain.Member;
import com.backend.taximate.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    List<Post> findAllByAuthor(Member author);
    Optional<Post> findByIdAndIsDeletedFalse(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    List<Post> findAllActive();

    @Query("SELECT p FROM Post p JOIN p.memberPosts mp WHERE mp.member.email = :email AND mp.post.isDeleted = false")
    Page<Post> findAllByMemberEmail(@Param("email") String email, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN p.memberPosts mp WHERE mp.member.email = :email AND mp.post.isDeleted = false")
    Page<Post> findAllByMemberAndIsDeletedFalse(@Param("email") String email, Pageable pageable);
}

