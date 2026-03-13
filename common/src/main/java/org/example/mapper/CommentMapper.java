package org.example.mapper;

import org.example.dto.CommentDto;
import org.example.model.Comment;
import org.example.model.User;
import org.example.model.Property;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setComment(comment.getComment());
        commentDto.setCreatedAt(comment.getCreatedAt());
        if(comment.getUser() != null) {
            commentDto.setUserId(comment.getUser().getId());
        }
        if(comment.getProperty() != null) {
            commentDto.setPropertyId(comment.getProperty().getId());
        }
        return commentDto;
    }
    public static Comment toEntity(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setComment(commentDto.getComment());
        comment.setCreatedAt(commentDto.getCreatedAt());
        if(commentDto.getUserId() != 0) {
            User user = new User();
            user.setId(commentDto.getUserId());
            comment.setUser(user);
        }
        if(commentDto.getPropertyId() != 0) {
            Property property = new Property();
            property.setId(commentDto.getPropertyId());
            comment.setProperty(property);
        }
        return comment;
    }
}
