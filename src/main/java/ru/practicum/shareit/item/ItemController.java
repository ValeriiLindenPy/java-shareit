package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
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
    public ItemDto getOne(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAllByText(@RequestParam("text") String text) {
        //todo only available
        return itemService.searchByText(text);
    }

    @PatchMapping("/{id}")
    public ItemDto getOne(@PathVariable Long id,@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.editOne(id, item, userId);
    }
}
