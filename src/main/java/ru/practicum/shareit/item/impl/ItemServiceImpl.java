package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.OwnerException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemOwnerDto> getAll(Long userId) {
        isUserExist(userId);

        List<Item> items = itemRepository.findItemsByOwnerId(userId);

        // Извлекаем идентификаторы вещей
        List<Long> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        // Получаем все бронирования для этих вещей одним запросом
        List<Booking> bookings = bookingRepository.findByItemId(itemIds);

        // Группируем бронирования по идентификатору вещи (не знаю как лучше так или через репозиторий)
        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        // Преобразуем список вещей в DTO с добавлением информации о бронированиях
        return items.stream().map(item -> {
            List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), Collections.emptyList());

            // Определяем даты последнего и ближайшего бронирования
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastBooking = itemBookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .map(Booking::getEnd)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime nextBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .map(Booking::getStart)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);


            return ItemMapper.toItemOwnerDto(item, lastBooking, nextBooking);
        }).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(ItemMapper::toItemDto)
                .orElseThrow(
                        () -> new NotFoundException("Item with id - %d not found"
                                .formatted(itemId))
                );
    }


    @Override
    public ItemDto editOne(Long id, ItemDto item, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
        );

        Item oldItem = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item with id - %d not found"
                        .formatted(id))
        );

        if (!Objects.equals(oldItem.getOwner().getId(), user.getId())) {
            throw new OwnerException("Item with id %d does not belong to user with id %d"
                    .formatted(id, user.getId()));
        }

        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        String lowerText = text.toLowerCase();

        return itemRepository.search(lowerText).stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto item, Long userId) {
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                )
        );
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public CommentRespondDto createComment(CommentRequestDto commentRequestDto, Long userId, Long itemId) {
        User author = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
        );

        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Item with id - %d not found"
                        .formatted(itemId))
        );

        Comment comment = Comment.builder()
                .author(author)
                .text(commentRequestDto.getText())
                .item(item)
                .build();


        return CommentMapper.toRespondDto(commentRepository.save(comment));
    }


    private void isUserExist(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                );
    }
}
