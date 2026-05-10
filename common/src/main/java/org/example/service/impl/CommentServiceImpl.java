package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.comment.PropertyCommentViewDto;
import org.example.exception.BusinessException;
import org.example.exception.ErrorCode;
import org.example.model.Comment;
import org.example.model.Property;
import org.example.model.User;
import org.example.repository.CommentRepository;
import org.example.repository.PropertyRepository;
import org.example.repository.UserRepository;
import org.example.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final int MAX_COMMENT_LENGTH = 4000;

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public void addComment(String text, int userId, int propertyId) {
        addPublicComment(userId, propertyId, text);
    }

    @Override
    @Transactional
    public void addPublicComment(int authorUserId, int propertyId, String text) {
        validateCommentText(text);
        User user = userRepository.findById(authorUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, authorUserId));
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROPERTY_NOT_FOUND, propertyId));

        Comment comment = new Comment();
        comment.setComment(text.trim());
        comment.setUser(user);
        comment.setProperty(property);
        comment.setCreatedAt(LocalDate.now());
        commentRepository.save(comment);
    }

    @Override
    public List<PropertyCommentViewDto> listPublicCommentsForProperty(int propertyId) {
        return commentRepository.findForPropertyWithUserOrderByCreatedAtDesc(propertyId).stream()
                .map(this::toView)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Long> getCommentCountsForPropertyIds(Collection<Integer> propertyIds) {
        if (propertyIds == null || propertyIds.isEmpty()) {
            return Map.of();
        }
        List<Integer> ids = propertyIds.stream().distinct().toList();
        List<Object[]> rows = commentRepository.countGroupedByPropertyIds(ids);
        Map<Integer, Long> map = new HashMap<>();
        for (Object[] row : rows) {
            map.put(((Number) row[0]).intValue(), ((Number) row[1]).longValue());
        }
        for (Integer id : ids) {
            map.putIfAbsent(id, 0L);
        }
        return map;
    }

    @Override
    public List<Comment> findAllByPropertyId(int propertyId) {
        return commentRepository.findForPropertyWithUserOrderByCreatedAtDesc(propertyId);
    }

    @Override
    public void deleteById(int id) {
        commentRepository.deleteById(id);
    }

    @Override
    public List<Comment> findAllByUserId(int userId) {
        return commentRepository.findAllByUser_Id(userId);
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

    private static void validateCommentText(String text) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(ErrorCode.COMMENT_TEXT_EMPTY);
        }
        if (text.length() > MAX_COMMENT_LENGTH) {
            throw new BusinessException(ErrorCode.COMMENT_TEXT_TOO_LONG);
        }
    }

    private PropertyCommentViewDto toView(Comment c) {
        User u = c.getUser();
        return new PropertyCommentViewDto(
                c.getId(),
                c.getComment(),
                c.getCreatedAt(),
                u.getId(),
                authorLabel(u)
        );
    }

    private static String authorLabel(User u) {
        String first = u.getName() != null ? u.getName().trim() : "";
        String last = u.getSurname() != null ? u.getSurname().trim() : "";
        String combined = (first + " " + last).trim();
        if (!combined.isEmpty()) {
            return combined;
        }
        return u.getEmail() != null ? u.getEmail() : ("User #" + u.getId());
    }
}
