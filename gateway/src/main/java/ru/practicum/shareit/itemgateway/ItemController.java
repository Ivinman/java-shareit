package ru.practicum.shareit.itemgateway;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemgateway.comment.CommentDto;
import ru.practicum.shareit.itemgateway.dto.ItemDto;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @RequestBody ItemDto itemDto) throws Exception {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> editItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                         @PathVariable Integer itemId,
                         @RequestBody ItemDto itemDto) throws Exception {
        return itemClient.editItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Integer itemId) {
        return itemClient.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearchedItems(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                   @RequestParam String text) {
        return itemClient.getSearchedItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer itemId,
                              @RequestBody CommentDto commentDto) throws Exception {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}