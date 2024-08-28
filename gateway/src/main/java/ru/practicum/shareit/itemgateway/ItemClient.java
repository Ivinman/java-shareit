package ru.practicum.shareit.itemgateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptiongateway.ValidationException;
import ru.practicum.shareit.itemgateway.comment.CommentDto;
import ru.practicum.shareit.itemgateway.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Integer userId, ItemDto itemDto) throws Exception {
        if (itemDto.getName() == null
                || itemDto.getName().isBlank()
                || itemDto.getName().isEmpty()) {
            throw new ValidationException("Название предмета не заполнено");
        }
        if (itemDto.getDescription() == null
                || itemDto.getDescription().isBlank()
                || itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание предмета не заполнено");
        }
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Доступность предмета не указана");
        }
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> editItem(Integer userId, Integer itemId, ItemDto itemDto) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> getItemById(Integer itemId) {
        return get("/" + itemId);
    }

    public ResponseEntity<Object> getItemsByOwnerId(Integer userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getSearchedItems(Integer userId, String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search", Long.valueOf(userId), parameters);
    }

    public ResponseEntity<Object> addComment(Integer userId, Integer itemId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
