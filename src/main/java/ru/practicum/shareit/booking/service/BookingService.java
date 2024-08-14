package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;

import java.util.List;

public interface BookingService {
    Booking addBooking(Integer userId, BookingDto bookingDto) throws Exception;
    Booking patchBooking(Integer userId, Integer bookingId, Boolean approved) throws Exception;
    Booking getBooking(Integer userId, Integer bookingId) throws Exception;
    List<Booking> getAllBookings(Integer userId, BookingStatus state);
    List<Booking> getAllBookingsByOwner(Integer userId, BookingStatus state);
}
