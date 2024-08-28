package ru.practicum.shareit.bookinggateway.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Integer itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
