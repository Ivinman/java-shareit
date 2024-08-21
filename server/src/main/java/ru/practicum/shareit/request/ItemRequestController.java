package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequest addRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUsersRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getUsersRequests(userId);
    }

    @GetMapping("/all")
    public Set<ItemRequestRespDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @PathVariable Integer requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}