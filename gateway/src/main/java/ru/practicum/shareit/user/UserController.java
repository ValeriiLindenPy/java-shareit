package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ValidationMarker;
import ru.practicum.shareit.user.dto.UserDto;


@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        log.info("get user with id - {}", id);
        return userClient.getUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> editById(@PathVariable Long id,@Validated(ValidationMarker.OnUpdate.class)
    @RequestBody UserDto userDto) {
        log.info("edit user with id - {}, UserDto - {}", id, userDto.toString());
        return userClient.editUser(id, userDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Validated(ValidationMarker.OnCreate.class)
                              @RequestBody UserDto userDto) {
        log.info("create user , UserDto - {}", userDto.toString());
        return userClient.createUser(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        log.info("delete user with id - {}", id);
        return userClient.deleteUser(id);
    }
}
