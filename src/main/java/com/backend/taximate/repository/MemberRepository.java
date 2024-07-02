package com.backend.taximate.repository;

import com.backend.taximate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.email = :email AND m.isDeleted = false")
    Boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.nickname = :nickname AND m.isDeleted = false")
    Boolean existsByNickname(@Param("nickname") String nickname);

    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("SELECT m FROM Member m WHERE m.isDeleted = false")
    List<Member> findAllActive();

    @Query("SELECT m FROM Member m WHERE m.isDeleted = true AND m.cancellationDate < :cutoffDate")
    List<Member> findByIsDeletedTrueAndCancellationDateBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}
