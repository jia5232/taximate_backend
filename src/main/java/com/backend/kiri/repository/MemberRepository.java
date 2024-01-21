package com.backend.kiri.repository;

import com.backend.kiri.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Boolean existsByUsername(String username);
    Member findByUsername(String username);
}
