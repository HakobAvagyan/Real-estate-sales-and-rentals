package org.example.repository;

import org.example.model.Conversation;
import org.example.model.enums.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    @Query("SELECT c FROM Conversation c WHERE (c.user1.id = :u1 AND c.user2.id = :u2) " +
            "OR (c.user1.id = :u2 AND c.user2.id = :u1)")
    Optional<Conversation> findExistingConversation(@Param("u1") int u1, @Param("u2") int u2);

    @Query("SELECT DISTINCT c FROM Conversation c "
            + "JOIN FETCH c.user1 JOIN FETCH c.user2 LEFT JOIN FETCH c.property "
            + "WHERE c.user1.id = :userId OR c.user2.id = :userId")
    List<Conversation> findAllByUserId(@Param("userId") int userId);

    Optional<Conversation> findByConversationTypeAndUser1IdAndUser2IdAndPropertyIsNull(
            ConversationType type,
            int user1Id,
            int user2Id);

    Optional<Conversation> findByConversationTypeAndUser1IdAndUser2IdAndPropertyId(
            ConversationType type,
            int user1Id,
            int user2Id,
            int propertyId);

    @Query("SELECT c FROM Conversation c WHERE c.conversationType = :supportType "
            + "AND (c.user1.id = :clientId OR c.user2.id = :clientId)")
    Optional<Conversation> findSupportForClient(
            @Param("supportType") ConversationType supportType,
            @Param("clientId") int clientId);
}
