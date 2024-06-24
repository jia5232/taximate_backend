package com.backend.kiri.repository;

import com.backend.kiri.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Optional<Post> findByIdAndIsDeletedFalse(Long postId);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false")
    List<Post> findAllActive();
}

