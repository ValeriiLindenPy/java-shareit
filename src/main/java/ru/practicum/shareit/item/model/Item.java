package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Item {
    /**
     * уникальный идентификатор вещи
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * краткое название;
     */
    private String name;
    /**
     * развёрнутое описание
     */
    private String description;
    /**
     * статус о том, доступна или нет вещь для аренды;
     */
    private Boolean available;
    /**
     * владелец вещи
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
    /**
     * если вещь была создана по запросу другого пользователя, то в этом
     * поле будет храниться ссылка на соответствующий запрос
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}
