package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest //(properties = "jdbc.url=jdbc:postgresql://localhost:5432/test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private final ItemRequestService itemRequestService;

    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto newItemRequestDto;
    private ItemRequest itemRequest;
    private final UserService userService;
    private final UserRepository userRepository;
    private Integer userId;
    private Integer newUserId;
    private final ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp(WebApplicationContext wac) throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Request");
        itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        newItemRequestDto = new ItemRequestDto();
        newItemRequestDto.setDescription("new Request");

        UserDto userDto = new UserDto("user", "user@user.com");
        userService.createUser(userDto);
        userId = userRepository.findByEmail(userDto.getEmail()).getId();

        UserDto newUserDto = new UserDto("New user", "newuser@user.com");
        userService.createUser(newUserDto);
        newUserId = userRepository.findByEmail(userDto.getEmail()).getId();
    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.addRequest(anyInt(), any())).thenReturn(itemRequest);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getUsersRequests() throws Exception {
        List<ItemRequestDto> itemRequestDtoList = List.of(itemRequestDto);
        when(itemRequestService.getUsersRequests(anyInt())).thenReturn(itemRequestDtoList);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(
                        secondRequest -> mockMvc.perform(post("/requests")
                                .content(objectMapper.writeValueAsString(newItemRequestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", newUserId))
                ).andDo(
                        result -> mockMvc.perform(get("/requests")
                                .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                );
    }

    @Test
    void getAllRequests() throws Exception {
        Set<ItemRequestRespDto> itemRequestRespDtoSet = ItemRequestMapper.itemRequestRespDtoList(itemRequestRepository, userId);
        when(itemRequestService.getAllRequests(anyInt())).thenReturn(itemRequestRespDtoSet);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(
                        secondRequest -> mockMvc.perform(post("/requests")
                                .content(objectMapper.writeValueAsString(newItemRequestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", newUserId))
                ).andDo(
                        result -> mockMvc.perform(get("/requests/all")
                                        .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                );
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyInt(), anyInt())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(
                        secondRequest -> mockMvc.perform(post("/requests")
                                .content(objectMapper.writeValueAsString(newItemRequestDto))
                                .characterEncoding(StandardCharsets.UTF_8)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", newUserId))
                ).andDo(
                        result -> mockMvc.perform(get("/requests/{requestId}",
                                        itemRequestRepository.findByUserId(userId, Sort.unsorted()).getFirst().getId())
                                        .header("X-Sharer-User-Id", userId))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                );
    }
}