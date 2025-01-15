package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.booking.Booking;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getAll(Long userId) {
        isUserExist(userId);
        return itemRepository.findItemsByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemOwnerDto getById(Long itemId) {
        return itemRepository.findById(itemId)
                .map(item -> ItemMapper.toItemOwnerDto(item, null, null))
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

    @Override
    public ItemOwnerDto getByIdAndOwnerId(Long id, Long userId) {

        Item item = itemRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Item with id - %d not found"
                        .formatted(id))
        );

        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new OwnerException("Item with id %d does not belong to user with id %d"
                    .formatted(id, userId));
        }


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastBookingDate = bookingRepository.findLastBooking(item.getId(), now)
                .map(Booking::getEnd)
                .orElse(null);

        LocalDateTime nextBookingDate = bookingRepository.findNextBooking(item.getId(), now)
                .map(Booking::getStart)
                .orElse(null);

        ItemOwnerDto itemOwnerDto = ItemMapper.toItemOwnerDto(item, lastBookingDate, nextBookingDate);

        itemOwnerDto.setComments(commentRepository.findByItemId(item.getId()));

        return itemOwnerDto;
    }


    private void isUserExist(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                );
    }
}
