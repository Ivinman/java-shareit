package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Integer userId, ItemDto itemDto);

    Item editItem(Integer userId, Integer itemId, ItemDto itemDto);

    Item getItemById(Integer itemId);

    List<Item> getItemsByOwnerId(Integer userId);

    List<Item> getSearchedItems(String text);
}
