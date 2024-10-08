package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.enums.BookingStatus;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @RequestBody BookingDto bookingDto) throws Exception {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking patchBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @PathVariable Integer bookingId,
                             @RequestParam Boolean approved) throws Exception {
        return bookingService.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer bookingId) throws Exception {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getAllBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @RequestParam(defaultValue = "ALL") BookingStatus state) {
        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(defaultValue = "ALL") BookingStatus state) {
        return bookingService.getAllBookingsByOwner(userId, state);
    }
}
