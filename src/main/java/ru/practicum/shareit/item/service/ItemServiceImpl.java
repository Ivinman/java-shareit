package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDateAndCommDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public Item addItem(Integer userId, ItemDto itemDto) throws Exception {
        if (userRepository.findById(userId).isEmpty()) {
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

        Item item = ItemMapper.toItem(itemDto, userId);
        item.setUser(userRepository.findById(userId).get());
        return itemRepository.save(item);
    }

    @Override
    public Item editItem(Integer userId, Integer itemId, ItemDto itemDto) throws Exception {
        if (!itemRepository.findById(itemId).get().getUser().getId().equals(userId)) {
            throw new OwnerException("Пользователь не является владельцем");
        }


        Item item = itemRepository.findById(itemId).get();
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.save(item);
    }

    @Override
    public ItemDateAndCommDto getItemById(Integer itemId) {
        return ItemMapper.toItemDateDto(itemRepository.findById(itemId).get(), bookingRepository, commentRepository);
    }

    @Override
    public List<ItemDateAndCommDto> getItemsByOwnerId(Integer userId) {
        List<ItemDateAndCommDto> itemDateAndCommDtos = new ArrayList<>();
        for (Item item : itemRepository.findByUserId(userId)) {
            itemDateAndCommDtos.add(ItemMapper.toItemDateDto(item, bookingRepository, commentRepository));
        }
        return itemDateAndCommDtos;
    }

    @Override
    public List<Item> getSearchedItems(String text) {
        List<Item> items = new ArrayList<>();
        if (text.isEmpty() || text.isBlank()) {
            return items;
        }
        for (Item item : itemRepository.search(text)) {
            if (item.getAvailable()) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) throws Exception {
        if (!bookingService.getAllBookings(userId, BookingStatus.ALL).isEmpty()) {
            for (Booking booking : bookingService.getAllBookings(userId, BookingStatus.ALL)) {
                if (booking.getItem().getId().equals(itemId) && booking.getEnd().isBefore(LocalDateTime.now())) {
                    return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, itemId, userId,
                            itemRepository, userRepository)));
                }
                throw new ValidationException("Пользователь не брал эту вещь или бронь ещё не закончена");
            }
        }
        throw new NotFoundException("У пользователя нет броней");
    }
}
