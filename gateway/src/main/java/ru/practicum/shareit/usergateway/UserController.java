package ru.practicum.shareit.usergateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.usergateway.dto.UserDto;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) throws Exception {
        log.info("Поступление запроса на создание пользователя");
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
                                  @PathVariable Integer userId) throws Exception {
        log.info("Поступление запроса на обновление информации о пользователе");
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Поступление запроса на вывод списка всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Integer id) {
        log.info("Поступил запрос на получение пользователя по id");
        return userClient.getUserById(id);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> removeUser(@PathVariable("userId") Integer id) {
        log.info("Поступил запрос на удаление пользователя");
        return userClient.removeUser(id);
    }
}