package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(UserDto userDto);

    UserForTest updateUser(UserDto userDto, Integer userId);

    List<User> getAllUsers();

    UserForTest getUserById(Integer id);

    void removeUser(Integer id);
}