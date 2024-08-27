package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private BookingDto bookingDto;
    private BookingDto newBookingDto;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private Integer userId;
    private Integer newUserId;
    private Integer itemId;
    private final BookingRepository bookingRepository;
    private final Booking booking = new Booking();
    private final Booking newBooking = new Booking();

    @Mock
    private final BookingService bookingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac) throws Exception {
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
        UserDto userDto = new UserDto("user", "user@user.com");
        UserDto newUserDto = new UserDto("New user", "newuser@user.com");
        userService.createUser(userDto);
        userId = userRepository.findByEmail("user@user.com").getId();
        userService.createUser(newUserDto);
        newUserId = userRepository.findByEmail("newuser@user.com").getId();

        ItemDto itemDto = new ItemDto("item", "desc", true, null);
        ItemDto newItemDto = new ItemDto("New item", "New desc", true, null);
        itemService.addItem(userId, itemDto);
        itemId = itemRepository.findByUserId(userId).getFirst().getId();
        itemService.addItem(newUserId, newItemDto);
        Integer newItemId = itemRepository.findByUserId(newUserId).getFirst().getId();


        bookingDto = new BookingDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        newBookingDto = new BookingDto();
        newBookingDto.setItemId(newItemId);
        newBookingDto.setStart(LocalDateTime.now().plusDays(2));
        newBookingDto.setEnd(LocalDateTime.now().plusDays(3));
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(anyInt(), any())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id", is(itemId)));
    }

    @Test
    void patchBooking() throws Exception {
        when(bookingService.patchBooking(anyInt(),anyInt(), anyBoolean())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(
                        result -> mockMvc.perform(patch("/bookings/{bookingId}",
                                bookingRepository.findByItemId(itemId).getFirst().getId())
                                .param("approved", "true")
                                .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status", is(Status.APPROVED.toString())))
                );
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                .content(objectMapper.writeValueAsString(bookingDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId))

                .andDo(
                        secondRequest -> mockMvc.perform(post("/bookings")
                                .content(objectMapper.writeValueAsString(newBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", newUserId))
                ).andDo(
                        result -> mockMvc.perform(get("/bookings/{bookingId}",
                                        bookingRepository.findByItemId(itemId).getFirst().getId())
                                        .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id",
                                        is(bookingRepository.findByItemId(itemId).getFirst().getId())))
                );
    }

    @Test
    void getAllBookings() throws Exception {
        List<Booking> bookingList = List.of(booking, newBooking);
        when(bookingService.getAllBookings(anyInt(), any())).thenReturn(bookingList);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))

                .andDo(
                        secondRequest -> mockMvc.perform(post("/bookings")
                                .content(objectMapper.writeValueAsString(newBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", newUserId))
                ).andDo(
                        result -> mockMvc.perform(get("/bookings")
                                        .param("state", "ALL")
                                        .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                );
    }

    @Test
    void getAllBookingsByOwner() throws Exception {
        List<Booking> bookingList = List.of(booking, newBooking);
        when(bookingService.getAllBookings(anyInt(), any())).thenReturn(bookingList);

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", newUserId))

                .andDo(
                        secondRequest -> mockMvc.perform(post("/bookings")
                                .content(objectMapper.writeValueAsString(newBookingDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", userId))
                ).andDo(
                        result -> mockMvc.perform(get("/bookings/owner")
                                        .param("state", "ALL")
                                        .header("X-Sharer-User-Id", newUserId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0)))
                );
    }
}