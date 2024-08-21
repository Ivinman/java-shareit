package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemForRequest {
    private Integer itemId;
    private String name;
    private Integer userId;
}
