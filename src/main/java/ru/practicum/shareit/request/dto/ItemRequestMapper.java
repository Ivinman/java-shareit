package ru.practicum.shareit.request.dto;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public static Set<ItemRequestDto> toItemRequestDtoList(ItemRequestRepository itemRequestRepository, Integer userId,
                                                           ItemRepository itemRepository) {
        Set<ItemRequestDto> itemRequestDtoList = new HashSet<>();
        for (ItemRequest itemRequest : itemRequestRepository.findByUserId(userId,
                Sort.by(Sort.Direction.ASC, "created"))) {
            ItemRequestDto itemRequestDto = new ItemRequestDto();
            itemRequestDto.setDescription(itemRequest.getDescription());
            itemRequestDto.setCreated(itemRequest.getCreated());

            List<ItemForRequest> itemForRequestList = new ArrayList<>();
            ItemForRequest itemForRequest = new ItemForRequest();
            List<Item> itemByRequestId = itemRepository.findByItemRequestId(itemRequest.getId());
            if (!itemByRequestId.isEmpty()) {
                for (Item item : itemRepository.findByItemRequestId(itemRequest.getId())) {
                    itemForRequest.setItemId(item.getId());
                    itemForRequest.setItemName(item.getName());
                    itemForRequest.setUserId(item.getUser().getId());
                    itemForRequestList.add(itemForRequest);
                }
            }
            itemRequestDto.setRequestResp(itemForRequestList);
        }
        return itemRequestDtoList;
    }

    public static ItemRequestDto toItemRequestDto(Integer requestId, ItemRequestRepository itemRequestRepository,
                                                  ItemRepository itemRepository) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());

        List<ItemForRequest> itemForRequestList = new ArrayList<>();
        ItemForRequest itemForRequest = new ItemForRequest();
        List<Item> itemByRequestId = itemRepository.findByItemRequestId(itemRequest.getId());
        if (!itemByRequestId.isEmpty()) {
            for (Item item : itemRepository.findByItemRequestId(itemRequest.getId())) {
                itemForRequest.setItemId(item.getId());
                itemForRequest.setItemName(item.getName());
                itemForRequest.setUserId(item.getUser().getId());
                itemForRequestList.add(itemForRequest);
            }
        }
        itemRequestDto.setRequestResp(itemForRequestList);
        return itemRequestDto;
    }

    public static Set<ItemRequestRespDto> itemRequestRespDtoList(ItemRequestRepository itemRequestRepository,
                                                                 Integer userId) {
        Set<ItemRequestRespDto> itemRequestRespDtoList = new HashSet<>();
        ItemRequestRespDto itemRequestRespDto = new ItemRequestRespDto();
        for (ItemRequest itemRequest : itemRequestRepository.findByUserId(userId,
                Sort.by(Sort.Direction.ASC, "created"))) {
            itemRequestRespDto.setDescription(itemRequest.getDescription());
            itemRequestRespDto.setRequestor(itemRequest.getUser());
            itemRequestRespDto.setCreated(itemRequest.getCreated());
            itemRequestRespDtoList.add(itemRequestRespDto);
        }
        return itemRequestRespDtoList;
    }
}
