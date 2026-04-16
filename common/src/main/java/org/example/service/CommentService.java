package org.example.service;

import org.example.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    void addComment(String text, int userId, int propertyId);
    List<Comment> findAllByPropertyId(int propertyId);
    void deleteById(int id);
    List<Comment> findAllByUserId(int userId);
    Optional<Comment> findById(int id);
    List<Comment> findAllCommentsForSeller(int userId);
    List<Comment> findAll();
}
