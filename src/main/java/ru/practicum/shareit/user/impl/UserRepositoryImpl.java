package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> findOne(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }


    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId() == 0) {
            user.setId(getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    @Override
    public void clear() {
        users.clear();
    }

    private Long getId() {
        Long currentId = users.values().stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0);
        return ++currentId;
    }
}
