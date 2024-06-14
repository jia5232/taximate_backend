package com.backend.kiri.repository;

import com.backend.kiri.domain.ChatRoom;
import com.backend.kiri.domain.Message;
import com.backend.kiri.domain.MessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chatRoom = :chatRoom ORDER BY m.createdTime DESC")
    List<Message> findFirstByChatRoomCustom(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.chatRoom = :chatRoom ORDER BY m.createdTime DESC")
    List<Message> findFirstByChatRoomOrderByCreatedTimeDesc(@Param("chatRoom") ChatRoom chatRoom, Pageable pageable);

    List<Message> findByChatRoom(ChatRoom chatRoom);

    // 읽지않은 메시지 개수 조회용.
    int countByChatRoomAndTypeAndCreatedTimeAfter(ChatRoom chatRoom, MessageType type, LocalDateTime lastReadAt);

    // 메시지 페이지네이션용
    Page<Message> findByChatRoomIdAndIdLessThanOrderByIdDesc(Long chatRoomId, Long lastMessageId, Pageable pageable);

    // lastMessageId가 null일 경우에 사용!
    Page<Message> findByChatRoomIdOrderByIdDesc(Long chatRoomId, Pageable pageable);
}
