package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto getById(Long id);

    UserDto editById(Long id, UserDto userDto);

    UserDto create(UserDto userDto);

    void deleteById(Long id);

    void clear();
}
