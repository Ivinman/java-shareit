package ru.practicum.shareit.requestgateway.dto;

import lombok.Data;

@Data
public class ItemForRequest {
    private Integer itemId;
    private String itemName;
    private Integer userId;
}
