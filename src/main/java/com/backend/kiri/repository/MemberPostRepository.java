package com.backend.kiri.repository;

import com.backend.kiri.domain.Member;
import com.backend.kiri.domain.MemberPost;
import com.backend.kiri.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberPostRepository extends JpaRepository<MemberPost, Long> {
    Optional<MemberPost> findByMemberAndPost(Member member, Post post);
}
