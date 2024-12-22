package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    /**
     *  уникальный идентификатор пользователя
     */
    private Long id;
    /**
     *  имя или логин пользователя
     */
    private String name;
    /**
     * уникальный адрес электронной почты
     */
    private String email;
}
