package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(UserDto userDto);

    User updateUser(UserDto userDto, Integer userId);

    List<User> getAllUsers();

    User getUserById(Integer id);

    void removeUser(Integer id);
}