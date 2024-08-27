package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.enums.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public Item addItem(Integer userId, ItemDto itemDto) throws Exception {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Указаный пользователь не найден");
        }
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setUser(userRepository.findById(userId).get());
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }
        return itemRepository.save(item);
    }

    @Override
    public Item editItem(Integer userId, Integer itemId, ItemDto itemDto) throws Exception {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет с данным id не найден");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
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
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет с данным id не найден");
        }
        return ItemMapper.toItemDateDto(itemRepository.findById(itemId).get(), bookingRepository, commentRepository);
    }

    @Override
    public List<ItemDateAndCommDto> getItemsByOwnerId(Integer userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        List<ItemDateAndCommDto> itemDateAndCommDtos = new ArrayList<>();
        List<Item> itemList = itemRepository.findByUserId(userId);
        for (Item item : itemList) {
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
        List<Item> itemsFromRepository = itemRepository.search(text);
        for (Item item : itemsFromRepository) {
            if (item.getAvailable()) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public CommentDto addComment(Integer userId, Integer itemId, CommentDto commentDto) throws Exception {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Предмет с данным id не найден");
        }

        List<BookingStatus> bookingStatuses = List.of(FUTURE, CURRENT, PAST);

        if (!bookingRepository.findByBookerIdAndBookingStatusIn(userId, bookingStatuses,
                Sort.by(Sort.Direction.ASC, "start")).isEmpty()) {
            for (Booking booking : bookingRepository.findByBookerIdAndBookingStatusIn(userId, bookingStatuses,
                    Sort.by(Sort.Direction.ASC, "start"))) {
                if (booking.getItem().getId().equals(itemId)) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, itemId, userId,
                                itemRepository, userRepository)));
                    }
                    throw new ValidationException("Пользователь не брал эту вещь или бронь ещё не закончена");
                }
            }
        }
        throw new NotFoundException("У пользователя нет броней");
    }
}