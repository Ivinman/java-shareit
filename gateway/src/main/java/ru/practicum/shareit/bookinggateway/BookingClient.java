package ru.practicum.shareit.bookinggateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.bookinggateway.dto.BookingDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.enumsgateway.BookingStatus;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(Integer userId, BookingDto bookingDto) {
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> patchBooking(Integer userId, Integer bookingId, String approved) {
        Map<String, Object> parameters = Map.of(
                "approved", Boolean.valueOf(approved)
        );
        return patch("/" + bookingId + "?approved=" + approved, userId, parameters);
    }

    public ResponseEntity<Object> getBooking(Integer userId, Integer bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookings(Integer userId, BookingStatus state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("", Long.valueOf(userId), parameters);
    }

    public ResponseEntity<Object> getAllBookingsByOwner(Integer userId, BookingStatus state) {
        Map<String, Object> parameters = Map.of(
                "state", state.name()
        );
        return get("/owner", Long.valueOf(userId), parameters);
    }
}
