package ru.practicum.shareit.requestgateway.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {
    private String description;
    private LocalDateTime created;
    private List<ItemForRequest> requestResp;
}
