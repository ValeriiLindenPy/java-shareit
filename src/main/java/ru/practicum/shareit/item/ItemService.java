package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentRespondDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;


public interface ItemService {
    List<ItemDto> getAll(Long userId);

    ItemDto editOne(Long id, ItemDto item, Long userId);

    List<ItemDto> searchByText(String text);

    ItemDto create(ItemDto item, Long userId);

    CommentRespondDto createComment(CommentRequestDto commentRequestDto, Long userId, Long itemId);

    ItemOwnerDto getByIdAndOwnerId(Long id, Long userId);
}
