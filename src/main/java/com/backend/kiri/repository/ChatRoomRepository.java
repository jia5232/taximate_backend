package com.backend.kiri.repository;

import com.backend.kiri.domain.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN cr.post p " +
            "JOIN p.memberPosts mp " +
            "WHERE mp.member.id = :memberId AND " +
            "(cr.id > :lastId OR :lastId IS NULL) " +
            "ORDER BY cr.id ASC")
    Page<ChatRoom> findChatRoomsByMemberAfterLastId(@Param("memberId") Long memberId,
                                                    @Param("lastId") Long lastId,
                                                    Pageable pageable);
}
