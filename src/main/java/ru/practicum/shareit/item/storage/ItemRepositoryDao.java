package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryDao implements ItemRepository {
    private final Map<Integer, Item> items;
    private final UserRepository userRepository;
    private Integer id = 0;

    @Override
    public Item addItem(Integer userId, ItemDto itemDto) {
        id++;
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item editItem(Integer userId,Integer itemId, ItemDto itemDto) {
        Item editedItem = ItemMapper.toItem(itemDto, userId);
        editedItem.setId(itemId);
        if (editedItem.getName() == null) {
            editedItem.setName(items.get(itemId).getName());
        }
        if (editedItem.getDescription() == null) {
            editedItem.setDescription(items.get(itemId).getDescription());
        }
        if (editedItem.getAvailable() == null) {
            editedItem.setAvailable(items.get(itemId).getAvailable());
        }
        items.put(itemId, editedItem);
        return editedItem;
    }

    @Override
    public Item getItemById(Integer itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByOwnerId(Integer userId) {
        List<Item> ownerItems = new ArrayList<>();
        for(Item item : items.values()) {
            if (item.getOwnerId().equals(userId)) {
                ownerItems.add(item);
            }
        }
        return ownerItems;
    }

    @Override
    public List<Item> getSearchedItems(String text) {
        List<Item> itemSearched = new ArrayList<>();
        if (text == null || text.isBlank() || text.isEmpty()) {
            return itemSearched;
        }
        for (Item item : items.values()) {
            if ((item.getName().toLowerCase().contains(text.toLowerCase())
                    || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                && item.getAvailable()) {
                itemSearched.add(item);
            }
        }
        return itemSearched;
    }
}