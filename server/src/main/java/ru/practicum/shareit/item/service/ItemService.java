package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDateAndCommDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Integer userId, ItemDto itemDto) throws Exception;

    Item editItem(Integer userId, Integer itemId, ItemDto itemDto) throws Exception;

    ItemDateAndCommDto getItemById(Integer itemId);

    List<ItemDateAndCommDto> getItemsByOwnerId(Integer userId);

    List<Item> getSearchedItems(String text);

    CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) throws Exception;
}