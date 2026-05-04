package org.example.dto.comment;

import lombok.Value;

import java.time.LocalDate;

@Value
public class PropertyCommentViewDto {
    int id;
    String text;
    LocalDate createdAt;
    int userId;
    String authorDisplayName;
}
