package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest(properties = "jdbc.url=jdbc:postgresql://localhost:5432/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private ItemDto itemDto;
    private ItemDto newItemDto;
    private final UserService userService;
    private Integer userId;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


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
        //newUserId = userRepository.findByEmail(userDto.getEmail()).getId();
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
    void getItemById() {
    }

    @Test
    void getItemsByOwnerId() {
    }

    @Test
    void getSearchedItems() {
    }

    @Test
    void addComment() {
    }
}