package org.example.repository;

import org.example.model.MessageChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageChatRepository extends JpaRepository<MessageChat, Integer> {

    List<MessageChat> findAllByConversationIdOrderByCreatedAtAsc(int conversationId);

    @Modifying
    @Query("UPDATE MessageChat m SET m.isRead = true WHERE m.conversation.id = :cid AND m.user.id <> :readerId "
            + "AND m.isRead = false")
    void markReadForOthers(@Param("cid") int conversationId, @Param("readerId") int readerId);

    @Query("SELECT COUNT(m) FROM MessageChat m WHERE m.conversation.id = :cid AND m.user.id <> :readerId "
            + "AND m.isRead = false")
    long countUnreadForUser(@Param("cid") int cid, @Param("readerId") int readerId);
}
