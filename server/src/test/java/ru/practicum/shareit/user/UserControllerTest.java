package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@Transactional
@SpringBootTest //(properties = "jdbc.url=jdbc:postgresql://localhost:5432/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserRepository userRepository;

    @Mock
    private final UserService userService;

    private MockMvc mockMvc;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
        userDto = new UserDto("user", "user@user.com");
        user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
    }

    @Test
    void createUser() throws Exception {
        when(userService.createUser(any())).thenReturn(user);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        //userService.createUser(userDto);
        //TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        //User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        UserDto updateUserDto = new UserDto("Newuser", "newuser@user.com");
        User updatedUser = new User();
        updatedUser.setName(updateUserDto.getName());
        updatedUser.setName(updateUserDto.getEmail());
        when(userService.updateUser(any(), any())).thenReturn(updatedUser);

        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                        .andDo(
                                result -> mockMvc.perform(patch("/users/{userId}", userRepository
                                                .findByEmail(userDto.getEmail()).getId())
                                                .content(objectMapper.writeValueAsString(updateUserDto))
                                                .characterEncoding(StandardCharsets.UTF_8)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .accept(MediaType.APPLICATION_JSON))

                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.name", is(updateUserDto.getName())))
                                        .andExpect(jsonPath("$.email", is(updateUserDto.getEmail())))
                        );
        /*when(userService.updateUser(any(), any())).thenReturn(user);
        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));*/
    }

    @Test
    void getAllUsers() throws Exception {
        UserDto updateUserDto = new UserDto("Newuser", "newuser@user.com");
        List<User> returnedUserList = List.of(user);

        when(userService.getAllUsers()).thenReturn(returnedUserList);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(
                        secondUser -> mockMvc.perform(post("/users")
                                        .content(objectMapper.writeValueAsString(updateUserDto))
                                        .characterEncoding(StandardCharsets.UTF_8)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))

                                .andDo(
                                        result -> mockMvc.perform(get("/users"))
                                                .andExpect(status().isOk())
                                                .andExpect(jsonPath("$", hasSize(2)))

                                )
                );
    }

    @Test
    void getUserById() throws Exception {
        UserDto updateUserDto = new UserDto("Newuser", "newuser@user.com");

        when(userService.getUserById(any())).thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(
                        secondUser -> mockMvc.perform(post("/users")
                                        .content(objectMapper.writeValueAsString(updateUserDto))
                                        .characterEncoding(StandardCharsets.UTF_8)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .accept(MediaType.APPLICATION_JSON))

                                .andDo(
                                        result -> mockMvc.perform(get("/users/{id}", userRepository
                                                        .findByEmail(userDto.getEmail()).getId()))
                                                .andExpect(status().isOk())
                                                .andExpect(jsonPath("$.name", is(user.getName())))
                                                .andExpect(jsonPath("$.email", is(user.getEmail())))

                                )
                );
    }
}