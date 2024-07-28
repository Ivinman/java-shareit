package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer id = 0;

    @Override
    public User createUser(UserDto userDto) {
        id++;
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(UserDto userDto, Integer userId) {
        User user;
        if (userDto.getName() == null) {
            user = new User(users.get(userId).getName(), userDto.getEmail());
        } else if (userDto.getEmail() == null) {
            user = new User(userDto.getName(), users.get(userId).getEmail());
        } else {
            user = UserMapper.toUser(userDto);
        }
        user.setId(userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        return users.get(id);
    }

    @Override
    public void removeUser(Integer id) {
        users.remove(id);
    }
}