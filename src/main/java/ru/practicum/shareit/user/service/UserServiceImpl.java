package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(UserDto userDto) throws Exception {
        if (!validation(userDto)) {
            throw new ValidationException("Ошибка валидации");
        }
        if (userRepository.getAllUsers().contains(UserMapper.toUser(userDto))) {
            throw new AlreadyExistException("Данный пользователь уже добавлен");
        }
        return userRepository.createUser(userDto);
    }

    @Override
    public User updateUser(UserDto userDto, Integer userId) throws Exception {
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank() || userDto.getEmail().isEmpty() || !userDto.getEmail().contains("@")) {
                throw new ValidationException("Ошибка валидации");
            }
        }
        if (userRepository.getAllUsers().contains(UserMapper.toUser(userDto))) {
            if (!userRepository.getUserById(userId).getEmail().equals(userDto.getEmail())) {
                throw new AlreadyExistException("Почта уже используется");
            }
        }
        return userRepository.updateUser(userDto, userId);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.getUserById(id);
    }

    @Override
    public void removeUser(Integer id) {
        userRepository.removeUser(id);
    }

    private boolean validation(UserDto userDto) {
        return userDto.getEmail() != null
                && !userDto.getEmail().isBlank()
                && !userDto.getEmail().isEmpty()
                && userDto.getEmail().contains("@");
    }
}