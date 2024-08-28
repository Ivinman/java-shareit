package ru.practicum.shareit.request.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final EntityManager entityManager;
    private final ItemRequestRepository itemRequestRepository;

    private User user;
    private User user2;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        UserDto userDto2 = new UserDto("user2", "user2@user.com");
        userService.createUser(userDto2);
        TypedQuery<User> query2 = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        user2 = query2.setParameter("email", userDto.getEmail()).getSingleResult();

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("New request");
        itemRequestService.addRequest(user.getId(), itemRequestDto);

        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto.setDescription("New request");
        itemRequestService.addRequest(user2.getId(), itemRequestDto2);
    }

    @Test
    void addRequest() throws Exception {
        ItemRequest itemRequest = itemRequestRepository.findByUserId(user.getId(), Sort.unsorted()).getFirst();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getUser(), equalTo(user));

        ItemRequestDto itemRequestDtoNew = new ItemRequestDto();
        itemRequestDto.setDescription("New request with problem");
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.addRequest(44, itemRequestDtoNew));

        assertThat("Указаный пользователь не найден", equalTo(notFoundException.getMessage()));
    }

    @Test
    void getUsersRequests() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getUsersRequests(user.getId());

        assertThat(itemRequestDtoList.size(), equalTo(2));
    }

    @Test
    void getAllRequests() throws Exception {
        Set<ItemRequestRespDto> itemRequestRespDtos = itemRequestService.getAllRequests(user.getId());

        assertThat(itemRequestRespDtos.size(), equalTo(2));

    }

    @Test
    void getRequestById() throws Exception {
        assertThat(itemRequestDto.getDescription(), equalTo(itemRequestService.getRequestById(user2.getId(),
                itemRequestRepository.findByUserId(user2.getId(), Sort.unsorted()).getFirst().getId()).getDescription()));
    }
}