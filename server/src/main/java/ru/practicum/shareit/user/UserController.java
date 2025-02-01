package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("get user with id - {}", id);
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDto editById(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info("edit user with id - {}, UserDto - {}", id, userDto.toString());
        return userService.editById(id, userDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("create user , UserDto - {}", userDto.toString());
        return userService.create(userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        log.info("delete user with id - {}", id);
        userService.deleteById(id);
    }
}
