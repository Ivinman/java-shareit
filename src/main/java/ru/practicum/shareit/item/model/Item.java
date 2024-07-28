package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private final Integer ownerId;
    private ItemRequest request;
}