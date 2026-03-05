package org.example.repository;

import org.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByPropertyId(int propertyId);

    List<Comment> findAllByUserId(int userId);

    @Query("SELECT c FROM Comment c WHERE c.property.user.id = :userId")
    List<Comment> findAllCommentsForUserAsSeller(@Param("userId") int userId);
}
