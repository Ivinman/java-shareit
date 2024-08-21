package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "item_requests")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /*@ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;*/

    @Column(name = "created")
    private LocalDateTime created;
}
