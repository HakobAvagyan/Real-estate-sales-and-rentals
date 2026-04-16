package org.example.mapper.comment;

import org.example.dto.CommentDto;
import org.example.model.Comment;
import org.example.model.Property;
import org.example.model.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "propertyId", source = "property.id")
    public abstract CommentDto toDto(Comment comment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "property", ignore = true)
    public abstract Comment toEntity(CommentDto commentDto);

    @AfterMapping
    protected void afterToEntity(CommentDto commentDto, @MappingTarget Comment comment) {
        if (commentDto.getUserId() != 0) {
            User user = new User();
            user.setId(commentDto.getUserId());
            comment.setUser(user);
        }
        if (commentDto.getPropertyId() != 0) {
            Property property = new Property();
            property.setId(commentDto.getPropertyId());
            comment.setProperty(property);
        }
    }
}
