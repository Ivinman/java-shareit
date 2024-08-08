package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item addItem(Integer userId, ItemDto itemDto) throws Exception {
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException("Указаный пользователь не найден");
        }
        if (itemDto.getName() == null
                || itemDto.getName().isBlank()
                || itemDto.getName().isEmpty()
                || itemDto.getDescription() == null
                || itemDto.getDescription().isBlank()
                || itemDto.getDescription().isEmpty()
                || itemDto.getAvailable() == null) {
            throw new ValidationException("Ошибка валидации");
        }
        return itemRepository.addItem(userId, itemDto);
    }

    @Override
    public Item editItem(Integer userId, Integer itemId, ItemDto itemDto) throws Exception {
        if (!itemRepository.getItemById(itemId).getOwnerId().equals(userId)) {
            throw new OwnerException("Пользователь не является владельцем");
        }
        return itemRepository.editItem(userId, itemId, itemDto);
    }

    @Override
    public Item getItemById(Integer itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getItemsByOwnerId(Integer userId) {
        return itemRepository.getItemsByOwnerId(userId);
    }

    @Override
    public List<Item> getSearchedItems(String text) {
        return itemRepository.getSearchedItems(text);
    }
}
