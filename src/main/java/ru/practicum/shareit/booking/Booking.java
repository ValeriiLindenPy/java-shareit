package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
public class Booking {
    /**
     * уникальный идентификатор бронирования
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * дата и время начала бронирования
     */
    private LocalDateTime start;
    /**
     * дата и время конца бронирования
     */
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    /**
     * пользователь, который осуществляет бронирование
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User booker;
    /**
     * статус бронирования. Может принимать одно из следующих
     * значений:
     * {@link BookingStatus#WAITING} — новое бронирование,ожидает одобрения,
     * {@link BookingStatus#APPROVED} — бронирование подтверждено владельцем,
     * {@link BookingStatus#REJECTED} — бронирование отклонено владельцем,
     * {@link BookingStatus#CANCELED} — бронирование отменено создателем.
     */
    @Enumerated
    private BookingStatus status;
}
