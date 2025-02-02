package ru.practicum.shareit.user.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.DublicatingEmailException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        user1 = userRepository.save(User.builder()
                .name("Sam")
                .email("sam@mail.com")
                .build());

        user2 = userRepository.save(User.builder()
                .name("Alice")
                .email("alice@mail.com")
                .build());
    }

    @Test
    void whenGetById_thenReturnUserDto() {
        UserDto found = userService.getById(user1.getId());
        assertNotNull(found);
        assertEquals(user1.getId(), found.getId());
        assertEquals(user1.getName(), found.getName());
        assertEquals(user1.getEmail(), found.getEmail());
    }

    @Test
    void whenGetByIdForNonExistingUser_thenThrowNotFoundException() {
        long nonExistingId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getById(nonExistingId));
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistingId)));
    }

    @Test
    void whenEditById_thenReturnEditedUserDto() {
        UserDto update = UserDto.builder()
                .name("Samuel")
                .email("samuel@mail.com")
                .build();
        UserDto updated = userService.editById(user1.getId(), update);
        assertNotNull(updated);
        assertEquals("Samuel", updated.getName());
        assertEquals("samuel@mail.com", updated.getEmail());
    }

    @Test
    void whenEditByIdWithDuplicateEmail_thenThrowDublicatingEmailException() {
        UserDto update = UserDto.builder()
                .email(user2.getEmail())
                .build();

        DublicatingEmailException exception = assertThrows(DublicatingEmailException.class,
                () -> userService.editById(user1.getId(), update));
        assertTrue(exception.getMessage().contains(user2.getEmail()));
    }

    @Test
    void whenCreateUser_thenReturnCreatedUserDto() {
        UserDto newUser = UserDto.builder()
                .name("Bob")
                .email("bob@mail.com")
                .build();
        UserDto created = userService.create(newUser);
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Bob", created.getName());
        assertEquals("bob@mail.com", created.getEmail());
    }

    @Test
    void whenCreateUserWithDuplicateEmail_thenThrowDublicatingEmailException() {
        UserDto newUser = UserDto.builder()
                .name("Another Sam")
                .email(user1.getEmail())
                .build();
        DublicatingEmailException exception = assertThrows(DublicatingEmailException.class,
                () -> userService.create(newUser));
        assertTrue(exception.getMessage().contains(user1.getEmail()));
    }

    @Test
    void whenDeleteById_thenUserIsDeleted() {
        userService.deleteById(user1.getId());
        assertThrows(NotFoundException.class, () -> userService.getById(user1.getId()));
    }
}
