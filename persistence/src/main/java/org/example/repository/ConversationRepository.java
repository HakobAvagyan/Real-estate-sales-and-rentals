package org.example.repository;

import org.example.model.Conversation;
import org.example.model.enums.ConversationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.lastMessageAt DESC")
    List<Conversation> findAllByUserIdOrderByLastMessageAtDesc(@Param("userId") int userId);

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
