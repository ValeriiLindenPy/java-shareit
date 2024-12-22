package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

public interface UserRepository {

    Optional<UserDto> getOne(Long userId);
}
