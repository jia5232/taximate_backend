package com.backend.taximate.repository;

import com.backend.taximate.domain.Member;
import com.backend.taximate.domain.MemberPost;
import com.backend.taximate.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberPostRepository extends JpaRepository<MemberPost, Long> {
    Optional<MemberPost> findByMemberAndPost(Member member, Post post);
}
