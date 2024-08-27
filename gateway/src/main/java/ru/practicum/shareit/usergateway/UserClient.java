package ru.practicum.shareit.usergateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exceptiongateway.ValidationException;
import ru.practicum.shareit.usergateway.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) throws Exception {
        if (!validation(userDto)) {
            throw new ValidationException("Ошибка валидации");
        }
        return post("", userDto);
    }

    public ResponseEntity<Object> updateUser(UserDto userDto, Integer userId) {
        return patch("/" + userId, userDto);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }

    public ResponseEntity<Object> getUserById(Integer userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> removeUser(Integer id) {
        return delete("/" + id);
    }

    private boolean validation(UserDto userDto) {
        return userDto.getEmail() != null
                && !userDto.getEmail().isBlank()
                && !userDto.getEmail().isEmpty()
                && userDto.getEmail().contains("@");
    }
}
