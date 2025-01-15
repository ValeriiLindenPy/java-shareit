package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ValidationMarker;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentRespondDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto getOne(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @PathVariable Long id) {
        if (userId != null) {
            return itemService.getByIdAndOwnerId(id, userId);
        }
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAllByText(@RequestParam("text") String text) {
        return itemService.searchByText(text);
    }

    @PatchMapping("/{id}")
    public ItemDto editOne(@PathVariable Long id,
                           @RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.editOne(id, item, userId);
    }

    @PostMapping
    public ItemDto create(@Validated(ValidationMarker.OnCreate.class)
                          @RequestBody ItemDto item,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.create(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentRespondDto createComment(@Validated(ValidationMarker.OnCreate.class)
                                           @RequestBody CommentRequestDto commentRequestDto,
                                           @RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        return itemService.createComment(commentRequestDto, userId, itemId);
    }
}
