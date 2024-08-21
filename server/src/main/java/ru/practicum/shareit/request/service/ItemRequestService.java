package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Set;

public interface ItemRequestService {
    ItemRequest addRequest(Integer userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUsersRequests(Integer userId);

    Set<ItemRequestRespDto> getAllRequests(Integer userId);

    ItemRequestDto getRequestById(Integer userId, Integer requestId);
}
