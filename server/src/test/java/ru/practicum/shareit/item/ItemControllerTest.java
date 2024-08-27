package ru.practicum.shareit.item;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDateAndCommDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest //(properties = "jdbc.url=jdbc:postgresql://localhost:5432/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ItemDto itemDto;
    private ItemDto newItemDto;
    private final UserService userService;
    private Integer userId;
    private Integer newUserId;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;


    private final Item item = new Item();
    private final Item newItem = new Item();

    @Mock
    private final ItemService itemService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext wac) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
        itemDto = new ItemDto("item", "desc", true, null);
        newItemDto = new ItemDto("New item", "New desc", true, null);

        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());

        newItem.setName(newItemDto.getName());
        newItem.setDescription(newItemDto.getDescription());


        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        userId = userRepository.findByEmail(userDto.getEmail()).getId();

        UserDto newUserDto = new UserDto("New user", "newuser@user.com");
        userService.createUser(newUserDto);
        newUserId = userRepository.findByEmail(userDto.getEmail()).getId();
    }

    @Test
    void addItem() throws Exception {
        when(itemService.addItem(anyInt(), any())).thenReturn(item);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @Test
    void editItem() throws Exception {
        when(itemService.editItem(anyInt(), anyInt(), any())).thenReturn(newItem);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(
                        result -> mockMvc.perform(patch("/items/{itemId}", itemRepository
                                .findByUserId(userId).getFirst().getId())
                                .content(objectMapper.writeValueAsString(newItemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is(newItemDto.getName())))
                                .andExpect(jsonPath("$.description", is(newItemDto.getDescription())))
                );


    }

    @Test
    void getItemById() throws Exception {
        ItemDateAndCommDto itemDateAndCommDto = new ItemDateAndCommDto();
        when(itemService.getItemById(anyInt())).thenReturn(itemDateAndCommDto);

        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId))

                .andDo(
                        secondRequest -> mockMvc.perform(post("/items")
                                .content(objectMapper.writeValueAsString(newItemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", userId))
                ).andDo(
                        result -> mockMvc.perform(get("/items/{itemId}",
                                itemRepository.findByUserId(userId).getFirst().getId())
                                .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                );
    }

    @Test
    void getItemsByOwnerId() throws Exception {
        List<ItemDateAndCommDto> itemDateAndCommDtoList = new ArrayList<>();
        when(itemService.getItemsByOwnerId(anyInt())).thenReturn(itemDateAndCommDtoList);

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))

                .andDo(
                        secondRequest -> mockMvc.perform(post("/items")
                                .content(objectMapper.writeValueAsString(newItemDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", newUserId))
                ).andDo(
                        result -> mockMvc.perform(get("/items")
                                .header("X-Sharer-User-Id", userId))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(item.getName())));
    }

    @Test
    void getSearchedItems() throws Exception {
        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId));
        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(newItemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", newUserId));

        List<Item> itemList = new ArrayList<>();
        when(itemService.getSearchedItems(anyString())).thenReturn(itemList);

        mockMvc.perform(get("/items/search")
                                        .param("text", "new")
                                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addComment() throws Exception {
        mockMvc.perform(post("/items")
                .content(objectMapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId));

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(itemRepository.findByUserId(userId).getFirst().getId());
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));

        bookingService.addBooking(newUserId, bookingDto);
        Booking booking = BookingMapper.toBooking(newUserId, bookingDto, itemRepository, userRepository);
        booking.setStart(LocalDateTime.now().minusDays(6));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        booking.setId(1);
        booking.setStatus(Status.APPROVED);
        booking.setBookingStatus(BookingStatus.PAST);
        bookingRepository.save(booking);

        CommentDto commentDto = new CommentDto(null, "comment", null, null);
        when(itemService.addComment(anyInt(), anyInt(), any())).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment",
                        itemRepository.findByUserId(userId).getFirst().getId())
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", newUserId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));

    }
}