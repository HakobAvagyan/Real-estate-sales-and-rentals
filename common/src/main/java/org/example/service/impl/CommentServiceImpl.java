package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.Comment;
import org.example.repository.CommentRepository;
import org.example.service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findAllByPropertyId(int propertyId) {
        return commentRepository.findAllByPropertyId(propertyId);
    }

    @Override
    public void deleteById(int id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<Comment> findAllByUserId(int userId) {
        return commentRepository.findAllByUserId(userId);
    }

    @Override
    public Optional<Comment> findById(int id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findAllCommentsForSeller(int userId) {
        return commentRepository.findAllCommentsForUserAsSeller(userId);
    }
    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }
}
