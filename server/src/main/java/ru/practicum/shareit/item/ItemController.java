package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    public final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{id}")
    public ItemOwnerDto getOne(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @PathVariable Long id) {
        log.info("get all item for userId - {}", userId);
        return itemService.getByIdAndOwnerId(id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAllByText(@RequestParam("text") String text) {
        log.info("get all items with text - {} ",text);
        return itemService.searchByText(text);
    }

    @PatchMapping("/{id}")
    public ItemDto editOne(@PathVariable Long id,
                           @RequestBody ItemDto item,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("edit item with id - {} for userId - {}, ItemDto - {}",id, userId, item.toString());
        return itemService.editOne(id, item, userId);
    }

    @PostMapping
    public ItemDto create(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("create item for userId - {}, ItemDto - {}", userId, item.toString());
        return itemService.create(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentRequestDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable Long itemId) {
        log.info("create comment for item with id - {} from userId - {}, CommentRequestDto - {}",
                itemId, userId, commentRequestDto.toString());
        return itemService.createComment(commentRequestDto, userId, itemId);
    }
}
