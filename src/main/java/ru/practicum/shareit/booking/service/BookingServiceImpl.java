package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static ru.practicum.shareit.enums.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking addBooking(Integer userId, BookingDto bookingDto) throws Exception {
        if ((bookingDto.getStart() == null) || bookingDto.getEnd() == null) {
            throw new ValidationException("Не заданы параметры времени");
        }
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Некорректное время конца брогирования");
        }
        if (bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new ValidationException("Старт и конец бронирования совпадают");
        }
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Некорректное время начала брогирования");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        if (itemRepository.findById(bookingDto.getItemId()).isEmpty()) {
            throw new NotFoundException("Предмет не найден");
        }
        if (!itemRepository.findById(bookingDto.getItemId()).get().getAvailable()) {
            throw new ValidationException("Предмет не доступен");
        }
        Booking booking = BookingMapper.toBooking(userId, bookingDto, itemRepository, userRepository);
        booking.setStatus(Status.WAITING);
        booking.setBookingStatus(BookingStatus.FUTURE);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking patchBooking(Integer userId, Integer bookingId, Boolean approved) throws ValidationException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ValidationException("Такого пользователя нет");
        }
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new NotFoundException("Такого бронирования нет");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new ValidationException("Не владелец вещи");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return booking;
    }

    @Override
    public Booking getBooking(Integer userId, Integer bookingId) throws ValidationException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new NotFoundException("Такого бронирования нет");
        }
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getUser().getId().equals(userId)) {
            return booking;
        } else {
            throw new ValidationException("Не тот пользователь");
        }
    }

    @Override
    public List<Booking> getAllBookings(Integer userId, BookingStatus state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        List<BookingStatus> bookingStatuses = List.of(FUTURE, CURRENT, PAST);
        if (state.equals(ALL)) {
            return bookingRepository.findByBookerIdAndBookingStatusIn(userId, bookingStatuses,
                    Sort.by(Sort.Direction.ASC, "start"));
        }
        return bookingRepository.findByBookerIdAndBookingStatusIn(userId, Collections.singleton(state),
                Sort.by(Sort.Direction.ASC, "start"));
    }

    @Override
    public List<Booking> getAllBookingsByOwner(Integer userId, BookingStatus state) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Такого пользователя нет");
        }
        return bookingRepository.findByUserId(userId, state.toString(),
                Sort.by(Sort.Direction.ASC, "start"));
    }
}