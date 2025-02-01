package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private UserDto user;
    private Long userId = 1L;

    @BeforeEach
    void setUp() {
         user = UserDto.builder()
                .id(userId)
                .name("Sam")
                .email("sam@mail.ru")
                .build();
    }

    @Test
    void getById() throws Exception {

        when(userService.getById(userId)).thenReturn(user);

        mockMvc.perform(
                get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).getById(userId);
    }

    @Test
    void editById() throws Exception {
        UserDto editedUser = UserDto.builder()
                .id(userId)
                .name("New")
                .email("newuser@mail.ru")
                .build();

        when(userService.editById(userId, editedUser)).thenReturn(editedUser);

        mockMvc.perform(
                        patch("/users/{id}", userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(editedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).editById(userId, editedUser);
    }

    @Test
    void create() throws Exception {

        when(userService.create(user)).thenReturn(user);

        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).create(user);
    }

    @Test
    void deleteById() throws Exception {

        doNothing().when(userService).deleteById(userId);

        mockMvc.perform(delete("/users/{id}", userId)).andExpect(status().isOk());

        verify(userService, times(1)).deleteById(userId);
    }
}