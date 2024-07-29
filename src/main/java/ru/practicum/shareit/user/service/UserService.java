package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForTest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(UserDto userDto) throws Exception;

    UserForTest updateUser(UserDto userDto, Integer userId) throws Exception;

    List<User> getAllUsers();

    UserForTest getUserById(Integer id);

    void removeUser(Integer id);
}