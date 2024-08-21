package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRespDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest addRequest(Integer userId, ItemRequestDto itemRequestDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Указаный пользователь не найден");
        }
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setUser(userRepository.findById(userId).get());
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUsersRequests(Integer userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Указаный пользователь не найден");
        }
        return ItemRequestMapper.toItemRequestDtoList(itemRequestRepository, userId, itemRepository);
    }

    @Override
    public Set<ItemRequestRespDto> getAllRequests(Integer userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Указаный пользователь не найден");
        }
        return ItemRequestMapper.itemRequestRespDtoList(itemRequestRepository, userId);
    }

    @Override
    public ItemRequestDto getRequestById(Integer userId, Integer requestId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Указаный пользователь не найден");
        }
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Указаный запрос не найден");
        }
        return ItemRequestMapper.toItemRequestDto(requestId, itemRequestRepository, itemRepository);
    }
}
