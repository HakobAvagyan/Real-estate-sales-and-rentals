package org.example.repository;

import org.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.property.id = :propertyId ORDER BY c.createdAt DESC, c.id DESC")
    List<Comment> findForPropertyWithUserOrderByCreatedAtDesc(@Param("propertyId") int propertyId);

    List<Comment> findAllByUser_Id(int userId);

    @Query("SELECT c.property.id, COUNT(c) FROM Comment c WHERE c.property.id IN :ids GROUP BY c.property.id")
    List<Object[]> countGroupedByPropertyIds(@Param("ids") Collection<Integer> ids);

    @Query("SELECT c FROM Comment c WHERE c.property.user.id = :userId")
    List<Comment> findAllCommentsForUserAsSeller(@Param("userId") int userId);
}
