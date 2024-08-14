package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

public class BookingMapper {
    public static Booking toBooking(Integer userId, BookingDto bookingDto,
                                    ItemRepository itemRepository,
                                    UserRepository userRepository) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(itemRepository.findById(bookingDto.getItemId()).get());
        booking.setBooker(userRepository.findById(userId).get());
        booking.setStatus(Status.WAITING);
        booking.setBookingStatus(BookingStatus.FUTURE);
        return booking;
    }
}
