package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ValidationMarker;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    public final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("get all item for userId - {}", userId);
        return itemClient.getOwnerItems(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOne(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId, @PathVariable Long id) {
        log.info("get item with id - {} for userId - {}",id, userId);
        return itemClient.getOwnerItem(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAllByText(@RequestParam("text") String text) {
        log.info("get all items with text - {} ",text);
        return itemClient.searchItems(text);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> editOne(@PathVariable Long id,
                                          @RequestBody ItemDto item,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("edit item with id - {} for userId - {}, ItemDto - {}",id, userId, item.toString());
        return itemClient.editItem(id, item, userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Validated(ValidationMarker.OnCreate.class)
                                         @RequestBody ItemDto item,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("create item for userId - {}, ItemDto - {}", userId, item.toString());
        return itemClient.createItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Validated(ValidationMarker.OnCreate.class)
                                                @RequestBody CommentRequestDto commentRequestDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long itemId) {
        log.info("create comment for item with id - {} from userId - {}, CommentRequestDto - {}",
                itemId, userId, commentRequestDto.toString());
        return itemClient.createComment(commentRequestDto, userId, itemId);
    }
}
