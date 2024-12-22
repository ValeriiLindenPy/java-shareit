package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;

    @Override
    public List<ItemDto> getAll(Long userId) {
        isUserExist(userId);
        return List.of();
    }

    @Override
    public ItemDto getById(Long itemId) {
        return null;
    }

    @Override
    public ItemDto editOne(Long id, ItemDto item, Long userId) {
        isUserExist(userId);
        return null;
    }

    @Override
    public List<ItemDto> searchByText(String text) {
        return List.of();
    }

    private void isUserExist(Long userId) {
        userRepository.getOne(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId)));
    }
}
