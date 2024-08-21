package ru.practicum.shareit.bookinggateway.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private Integer itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
