package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.storage.ItemRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest().getId()
        );
    }

    public static Item toItem(ItemDto itemDto, Integer ownerId) {
        Item newItem = new Item();
        newItem.setName(itemDto.getName());
        newItem.setDescription(itemDto.getDescription());
        newItem.setAvailable(itemDto.getAvailable());
        return newItem;
    }

    public static ItemDateAndCommDto toItemDateDto(Item item,
                                                   BookingRepository bookingRepository,
                                                   CommentRepository commentRepository) {
        ItemDateAndCommDto itemDateAndCommDto = new ItemDateAndCommDto();
        itemDateAndCommDto.setId(item.getId());
        itemDateAndCommDto.setName(item.getName());
        itemDateAndCommDto.setDescription(item.getDescription());
        itemDateAndCommDto.setAvailable(item.getAvailable());
        if (bookingRepository.findByItemId(item.getId()).isEmpty()) {
            itemDateAndCommDto.setLastBooking(null);
            itemDateAndCommDto.setNextBooking(null);
        }
        List<Booking> bookingMap = bookingRepository.findByItemId(item.getId());
        for (Booking booking : bookingMap) {
            if (booking.getStart().isAfter(LocalDateTime.now())) {
                itemDateAndCommDto.setLastBooking(bookingMap.get(booking.getId() - 1).getEnd());
                itemDateAndCommDto.setNextBooking(booking.getStart());
                break;
            }
        }
        itemDateAndCommDto.setComments(commentRepository.findByItemId(item.getId()));
        return itemDateAndCommDto;
    }
}