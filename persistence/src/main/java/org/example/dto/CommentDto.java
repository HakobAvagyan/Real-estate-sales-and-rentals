package org.example.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentDto {
    private int id;
    private String comment;
    private LocalDate createdAt;
    private int propertyId;
    private int userId;
}
