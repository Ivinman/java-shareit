package ru.practicum.shareit.item.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDateAndCommDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "jdbc.url=jdbc:postgresql://localhost:5432/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final UserService userService;
    private final EntityManager entityManager;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Test
    void addItem() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        ItemDto itemDto = new ItemDto("item", "description", true, null);
        itemService.addItem(user.getId(), itemDto);
        Item item = itemRepository.findByUserId(user.getId()).getFirst();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getUser().getId(), equalTo(user.getId()));

        ItemDto newItemDto = new ItemDto("New item", "New description", true, null);
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.addItem(44, newItemDto));

        assertThat("Указаный пользователь не найден", equalTo(notFoundException.getMessage()));
    }

    @Test
    void editItem() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        ItemDto itemDto = new ItemDto("item", "description", true, null);
        itemService.addItem(user.getId(), itemDto);

        ItemDto newItemDto = new ItemDto("New item", "New description", true, null);
        itemService.editItem(user.getId(), itemRepository.findByUserId(user.getId()).getFirst().getId(), newItemDto);

        Item item = itemRepository.findByUserId(user.getId()).getFirst();

        assertThat(item.getName(), equalTo(newItemDto.getName()));
        assertThat(item.getDescription(), equalTo(newItemDto.getDescription()));
        assertThat(item.getUser().getId(), equalTo(user.getId()));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.editItem(user.getId(), 44, newItemDto));

        assertThat("Предмет с данным id не найден", equalTo(notFoundException.getMessage()));

        UserDto newUserDto = new UserDto("New user", "newuser@user.com");
        userService.createUser(newUserDto);
        TypedQuery<User> newQuery = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User newUser = newQuery.setParameter("email", newUserDto.getEmail()).getSingleResult();
        OwnerException ownerException = assertThrows(OwnerException.class,
                () -> itemService.editItem(newUser.getId(),
                        itemRepository.findByUserId(user.getId()).getFirst().getId(),
                        newItemDto));

        assertThat("Пользователь не является владельцем", equalTo(ownerException.getMessage()));
    }

    @Test
    void getItemById() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        ItemDto itemDto = new ItemDto("item", "description", true, null);
        itemService.addItem(user.getId(), itemDto);
        ItemDto newItemDto = new ItemDto("New item", "New description", true, null);
        itemService.addItem(user.getId(), newItemDto);

        ItemDateAndCommDto item = itemService.getItemById(itemRepository.findByUserId(user.getId()).getFirst().getId());

        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));

    }

    @Test
    void getItemsByOwnerId() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        ItemDto itemDto = new ItemDto("item", "description", true, null);
        itemService.addItem(user.getId(), itemDto);

        UserDto newUserDto = new UserDto("New user", "newuser@user.com");
        userService.createUser(newUserDto);
        TypedQuery<User> newQuery = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User newUser = newQuery.setParameter("email", newUserDto.getEmail()).getSingleResult();
        ItemDto newItemDto = new ItemDto("New item", "New description", true, null);
        itemService.addItem(newUser.getId(), newItemDto);

        List<ItemDateAndCommDto> itemDateAndCommDtoList = itemService.getItemsByOwnerId(user.getId());

        assertThat(itemDateAndCommDtoList.size(), equalTo(1));
    }

    @Test
    void getSearchedItems() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        ItemDto itemDto = new ItemDto("item", "description", true, null);
        itemService.addItem(user.getId(), itemDto);
        ItemDto newItemDto = new ItemDto("New item", "New description", true, null);
        itemService.addItem(user.getId(), newItemDto);

        List<Item> item = itemService.getSearchedItems("new");

        assertThat(item.size(), equalTo(1));
        assertThat(item.getFirst().getName(), equalTo(newItemDto.getName()));
    }

    @Test
    void addComment() throws Exception {
        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        TypedQuery<User> query = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        ItemDto itemDto = new ItemDto("item", "description", true, null);
        itemService.addItem(user.getId(), itemDto);

        UserDto newUserDto = new UserDto("New user", "newuser@user.com");
        userService.createUser(newUserDto);
        TypedQuery<User> newQuery = entityManager.createQuery("select u from User u where u.email = :email", User.class);
        User newUser = newQuery.setParameter("email", newUserDto.getEmail()).getSingleResult();

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemRepository.findByUserId(user.getId()).getFirst().getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.addBooking(newUser.getId(), bookingDto);
        Booking booking = BookingMapper.toBooking(newUser.getId(), bookingDto, itemRepository, userRepository);
        booking.setStart(LocalDateTime.now().minusDays(6));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setId(1);
        booking.setStatus(Status.APPROVED);
        booking.setBookingStatus(BookingStatus.PAST);
        bookingRepository.save(booking);


        CommentDto commentDto = new CommentDto(1, "comment", newUser.getName(), Instant.now());
        CommentDto commentDto1 = itemService.addComment(newUser.getId(),
                itemRepository.findByUserId(user.getId()).getFirst().getId(),
                commentDto);

        assertThat(commentDto1.getText(), equalTo(commentDto.getText()));
        assertThat(commentDto1.getAuthorName(), equalTo(newUser.getName()));
    }
}