package com.backend.kiri.repository;

import com.backend.kiri.domain.Block;
import com.backend.kiri.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByBlocker(Member blocker);
    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);
}
