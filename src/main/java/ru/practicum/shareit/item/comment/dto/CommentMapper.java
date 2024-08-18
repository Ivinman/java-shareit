package ru.practicum.shareit.item.comment.dto;

import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.Instant;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto, Integer itemId, Integer userId,
                                    ItemRepository itemRepository, UserRepository userRepository) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(itemRepository.findById(itemId).get());
        comment.setAuthor(userRepository.findById(userId).get());
        comment.setCreated(Instant.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }
}
