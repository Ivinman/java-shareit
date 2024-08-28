package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    void createUser() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        UserDto userWithSameEmail = new UserDto("user3", "user@user.com");
        AlreadyExistException alreadyExistException = assertThrows(AlreadyExistException.class,
                () -> userService.createUser(userWithSameEmail));
        assertThat("Данный пользователь уже добавлен", equalTo(alreadyExistException.getMessage()));
    }

    @Test
    void updateUser() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        UserDto updatedUser = new UserDto("New user", "newuser@user.com");
        userService.updateUser(updatedUser, user.getId());
        TypedQuery<User> queryNew = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User userNew = queryNew.setParameter("email", updatedUser.getEmail()).getSingleResult();

        assertThat(userNew.getId(), notNullValue());
        assertThat(userNew.getName(), equalTo(updatedUser.getName()));
        assertThat(userNew.getEmail(), equalTo(updatedUser.getEmail()));

        UserDto updateUserWithSameEmail = new UserDto("New user", "newuser@user.com");
        AlreadyExistException alreadyExistException = assertThrows(AlreadyExistException.class,
                () -> userService.updateUser(updateUserWithSameEmail, userNew.getId()));
        assertThat("Почта уже используется", equalTo(alreadyExistException.getMessage()));
    }

    @Test
    void getAllUsers() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        UserDto user2 = new UserDto("New user", "newuser@user.com");
        userService.createUser(user2);

        List<User> userList = userService.getAllUsers();

        assertThat(userList.size(), equalTo(2));
    }

    @Test
    void getUserById() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        UserDto user2 = new UserDto("New user", "newuser@user.com");
        userService.createUser(user2);

        User userFromRep = userService.getUserById(user.getId());

        assertThat(userFromRep.getId(), notNullValue());
        assertThat(userFromRep.getName(), equalTo(user.getName()));
        assertThat(userFromRep.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void removeUser() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        userService.removeUser(user.getId());

        List<User> userList = userService.getAllUsers();

        assertThat(userList.size(), equalTo(0));
    }
}