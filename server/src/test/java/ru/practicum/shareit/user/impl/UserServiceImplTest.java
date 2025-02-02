package ru.practicum.shareit.user.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class UserServiceImplTest {
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build();

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            if (savedUser == null) {
                throw new IllegalArgumentException("Saved user cannot be null");
            }
            savedUser.setId(1L);
            return savedUser;
        });

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(List.of(user));

        user = userRepository.save(user);
    }

    @Test
    void whenEditById_ReturnNewDataUser() {
        UserDto newUser = UserDto.builder()
                .email("new@mail.com")
                .name("NewName")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UserDto editedUser = userService.editById(user.getId(), newUser);

        assertNotNull(editedUser);
        assertEquals(user.getId(), editedUser.getId());
    }
}
