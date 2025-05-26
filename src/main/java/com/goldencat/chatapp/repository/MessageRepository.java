package com.goldencat.chatapp.repository;

import com.goldencat.chatapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatId(String chatId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Message m WHERE m.senderId = :username OR m.receiverId = :username")
    void deleteAllByUsername(String username);
}
