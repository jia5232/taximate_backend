package com.backend.kiri.repository;

import com.backend.kiri.domain.ChatRoom;
import com.backend.kiri.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chatRoom = :chatRoom ORDER BY m.createdTime DESC")
    Message findFirstByChatRoomCustom(@Param("chatRoom") ChatRoom chatRoom);

    List<Message> findByChatRoom(ChatRoom chatRoom);
}
