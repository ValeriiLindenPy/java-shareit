package ru.practicum.shareit.request;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
public class ItemRequest {
    /**
     * уникальный идентификатор запроса
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * текст запроса, содержащий описание требуемой вещи
     */
    private String description;
    /**
     * пользователь, создавший запрос
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User requestor;
    /**
     *  дата и время создания запроса.
     */
    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}
