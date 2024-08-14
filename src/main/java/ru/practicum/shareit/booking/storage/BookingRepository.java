package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.BookingStatus;

import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdAndBookingStatusIn(Integer bookerId, Collection<BookingStatus> state, Sort sort);

    @Query("select b from Booking b " +
            "join Item i on i.id = b.item " +
            "join User u on i.user = u.id " +
            "where u.id = ?1 " +
            "and b.bookingStatus like '?2'")
    List<Booking> findByUserId(Integer userId, String state, Sort sort);

    List<Booking> findByItemId(Integer itemId);
}
