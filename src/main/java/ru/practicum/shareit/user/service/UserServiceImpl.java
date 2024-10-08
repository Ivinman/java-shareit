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
        for (User userFromRep : userRepository.findAll()) {
            if (userFromRep.getEmail().equals(userDto.getEmail())) {
                throw new AlreadyExistException("Данный пользователь уже добавлен");
            }
        }
        return userRepository.save(UserMapper.toUser(userDto));
    }

    @Override
    public User updateUser(UserDto userDto, Integer userId) throws Exception {
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank() || userDto.getEmail().isEmpty() || !userDto.getEmail().contains("@")) {
                throw new ValidationException("Ошибка валидации");
            }

            for (User userFromRep : userRepository.findAll()) {
                if (userFromRep.getEmail().equals(userDto.getEmail())) {
                    if (!userRepository.findById(userId).get().getEmail().equals(userDto.getEmail())) {
                        throw new AlreadyExistException("Почта уже используется");
                    }
                }
            }
        }
        User user = userRepository.findById(userId).get();
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).get();
    }

    @Override
    public void removeUser(Integer id) {
        userRepository.deleteById(id);
    }

    private boolean validation(UserDto userDto) {
        return userDto.getEmail() != null
                && !userDto.getEmail().isBlank()
                && !userDto.getEmail().isEmpty()
                && userDto.getEmail().contains("@");
    }
}