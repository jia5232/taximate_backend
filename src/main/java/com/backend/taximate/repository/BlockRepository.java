package com.backend.taximate.repository;

import com.backend.taximate.domain.Block;
import com.backend.taximate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlockRepository extends JpaRepository<Block, Long> {
    List<Block> findByBlocker(Member blocker);
    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);
}
