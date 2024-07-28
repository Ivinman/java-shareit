package ru.practicum.shareit.user.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class User {
    @EqualsAndHashCode.Exclude
    private Integer id;
    @EqualsAndHashCode.Exclude
    private final String name;
    private final String email;
}