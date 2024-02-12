package com.backend.kiri.repository;

import com.backend.kiri.domain.MemberPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPostRepository extends JpaRepository<MemberPost, Long> {
}
