package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> findAll() {
        return items.values().stream().toList();
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public List<Item> findAllByUser(Long userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), userId))
                .toList();
    }

    @Override
    public Optional<Item> findOne(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item save(Item newItem) {
        newItem.setId(getId());
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    @Override
    public Item update(Item newItem) {
        items.put(newItem.getId(), newItem);
        return newItem;
    }

    private Long getId() {
        Long currentId = items.values().stream()
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
