package ru.practicum.shareit.bookinggateway;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.bookinggateway.dto.BookingDto;

import ru.practicum.shareit.enumsgateway.BookingStatus;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                             @RequestBody BookingDto bookingDto) throws Exception {
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @PathVariable Integer bookingId,
                             @RequestParam String approved) throws Exception {
        return bookingClient.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer bookingId) throws Exception {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                        @RequestParam(defaultValue = "ALL") BookingStatus state) {
        return bookingClient.getAllBookings(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                               @RequestParam(defaultValue = "ALL") BookingStatus state) {
        return bookingClient.getAllBookingsByOwner(userId, state);
    }
}
