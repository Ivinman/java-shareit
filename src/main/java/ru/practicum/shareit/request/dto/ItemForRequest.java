package ru.practicum.shareit.request.dto;

import lombok.Data;

@Data
public class ItemForRequest {
    private Integer itemId;
    private String itemName;
    private Integer userId;
}
