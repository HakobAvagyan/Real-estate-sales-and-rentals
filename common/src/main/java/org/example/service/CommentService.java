package org.example.service;

import org.example.dto.comment.PropertyCommentViewDto;
import org.example.model.Comment;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CommentService {
    void addComment(String text, int userId, int propertyId);

    void addPublicComment(int authorUserId, int propertyId, String text);

    List<PropertyCommentViewDto> listPublicCommentsForProperty(int propertyId);

    Map<Integer, Long> getCommentCountsForPropertyIds(Collection<Integer> propertyIds);

    List<Comment> findAllByPropertyId(int propertyId);

    void deleteById(int id);

    List<Comment> findAllByUserId(int userId);

    Optional<Comment> findById(int id);

    List<Comment> findAllCommentsForSeller(int userId);

    List<Comment> findAll();
}
