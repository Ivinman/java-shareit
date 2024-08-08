package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Integer userId, ItemDto itemDto) throws Exception;

    Item editItem(Integer userId, Integer itemId, ItemDto itemDto) throws Exception;

    Item getItemById(Integer itemId);

    List<Item> getItemsByOwnerId(Integer userId);

    List<Item> getSearchedItems(String text);
}