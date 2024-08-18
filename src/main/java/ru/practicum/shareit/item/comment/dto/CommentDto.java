package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CommentDto {
    private final Integer id;
    private final String text;
    private final String authorName;
    private final Instant created;
}
