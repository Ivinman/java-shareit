package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = "jdbc:h2:file:./db/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final EntityManager entityManager;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Test
    void addBooking() throws Exception {
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
        Booking booking = bookingService.addBooking(newUser.getId(), bookingDto);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getItem().getId(), equalTo(bookingDto.getItemId()));
        assertThat(booking.getBooker(), equalTo(newUser));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(44, bookingDto));

        assertThat("Такого пользователя нет", equalTo(notFoundException.getMessage()));

        ItemDto newItemDto = new ItemDto("New item", "New description", false, null);
        itemService.addItem(newUser.getId(), newItemDto);
        BookingDto newBookingDto = new BookingDto();
        newBookingDto.setItemId(itemRepository.findByUserId(newUser.getId()).getFirst().getId());
        newBookingDto.setStart(LocalDateTime.now().plusDays(1));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(2));
        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(newUser.getId(), newBookingDto));

        assertThat("Предмет не доступен", equalTo(validationException.getMessage()));

        bookingDto.setItemId(44);
        NotFoundException itemNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(user.getId(), bookingDto));

        assertThat("Предмет не найден", equalTo(itemNotFoundException.getMessage()));
    }

    @Test
    void patchBooking() throws Exception {
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
        Booking booking = bookingService.addBooking(newUser.getId(), bookingDto);

        Booking patchbooking = bookingService.patchBooking(user.getId(),
                bookingRepository.findById(booking.getId()).get().getId(),
                true);

        assertThat(patchbooking.getId(), notNullValue());
        assertThat(patchbooking.getItem(), equalTo(booking.getItem()));
        assertThat(patchbooking.getBooker(), equalTo(booking.getBooker()));
        assertThat(patchbooking.getStatus(), equalTo(Status.APPROVED));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.patchBooking(user.getId(),
                        44,
                        true));

        assertThat("Такого бронирования нет", equalTo(notFoundException.getMessage()));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.patchBooking(newUser.getId(),
                        bookingRepository.findById(booking.getId()).get().getId(),
                        true));

        assertThat("Не владелец вещи", equalTo(validationException.getMessage()));

    }

    @Test
    void getBooking() throws Exception {
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
        Booking booking = bookingService.addBooking(newUser.getId(), bookingDto);

        Booking getBooking = bookingService.getBooking(user.getId(), booking.getId());

        assertThat(booking, equalTo(getBooking));
    }

    @Test
    void getAllBookings() throws Exception {
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
        Booking booking = bookingService.addBooking(newUser.getId(), bookingDto);

        List<Booking> bookingList = bookingService.getAllBookings(newUser.getId(), BookingStatus.ALL);

        assertThat(bookingList.size(), equalTo(1));
    }

    @Test
    void getAllBookingsByOwner() throws Exception {
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
        Booking booking = bookingService.addBooking(newUser.getId(), bookingDto);

        List<Booking> bookingList = bookingService.getAllBookings(newUser.getId(), BookingStatus.ALL);

        assertThat(bookingList.size(), equalTo(1));
    }
}
