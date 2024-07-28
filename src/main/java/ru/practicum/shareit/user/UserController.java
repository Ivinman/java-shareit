package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@RequestBody UserDto userDto) throws Exception {
        log.info("Поступление запроса на создание пользователя");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@RequestBody UserDto userDto,
                           @PathVariable Integer userId) throws Exception {
        log.info("Поступление запроса на обновление информации о пользователе");
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Поступление запроса на вывод списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        log.info("Поступил запрос на получение пользователя по id");
        return userService.getUserById(id);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable("userId") Integer id) {
        log.info("Поступил запрос на удаление пользователя");
        userService.removeUser(id);
    }
}