package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    private UserDto user;
    private Long userId = 1L;
    private ItemDto item1;
    private ItemDto item2;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .id(userId)
                .name("Sam")
                .email("sam@mail.ru")
                .build();

        item1 = ItemDto.builder()
                .id(1L)
                .name("item1")
                .description("Description1")
                .available(true)
                .build();

        item2 = ItemDto.builder()
                .id(2L)
                .name("item2")
                .description("Description2")
                .available(true)
                .build();
    }

    @Test
    void getAll() throws Exception {
        List<ItemDto> items = List.of(item1, item2);

        when(itemService.getAll(userId)).thenReturn(items);

        mockMvc.perform(get("/items").contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$").isArray());

        verify(itemService, times(1)).getAll(userId);
    }

    @Test
    void getOne() throws Exception {
        ItemOwnerDto item = ItemOwnerDto.builder()
                .id(3L)
                .name("item")
                .description("Description")
                .available(true)
                .build();

        when(itemService.getByIdAndOwnerId(item.getId(), userId)).thenReturn(item);

        mockMvc.perform(get("/items/{id}", item.getId())
                .header("X-Sharer-User-Id", userId))
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(status().isOk());

        verify(itemService, times(1)).getByIdAndOwnerId(item.getId(), userId);
    }

    @Test
    void searchAllByText() throws Exception {
        List<ItemDto> items = List.of(item1, item2);

        String text = "item";

        when(itemService.searchByText(text)).thenReturn(items);

        mockMvc.perform(get("/items/search").param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value(item1.getName()))
                .andExpect(jsonPath("$").isArray());

        verify(itemService, times(1)).searchByText(text);
    }

    @Test
    void editOne() throws Exception  {
        ItemDto editedItem = ItemDto.builder()
                .id(1L)
                .name("editedItem1")
                .description("editedItemDescription1")
                .available(true)
                .build();

        when(itemService.editOne(item1.getId(), editedItem, userId)).thenReturn(editedItem);

        mockMvc.perform(patch("/items/{id}", item1.getId())
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(editedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(editedItem.getName()));

        verify(itemService,times(1)).editOne(item1.getId(), editedItem, userId);
    }

    @Test
    void create() throws Exception  {

        when(itemService.create(item1, userId)).thenReturn(item1);

        mockMvc.perform(post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId)
                .content(new ObjectMapper().writeValueAsString(item1))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(item1.getName()));

        verify(itemService, times(1)).create(item1, userId);
    }

    @Test
    void createComment() throws Exception  {
        CommentRequestDto comment = CommentRequestDto.builder()
                .text("comment")
                .build();

        CommentResponseDto response = CommentResponseDto.builder()
                .id(1L)
                .text(comment.getText())
                .authorName(user.getName())
                .created(LocalDateTime.now())
                .build();

        when(itemService.createComment(comment,userId, item1.getId())).thenReturn(response);

        mockMvc.perform(post("/items/{itemId}/comment", item1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", userId)
                .content(new ObjectMapper().writeValueAsString(comment))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(user.getName()));

        verify(itemService, times(1)).createComment(comment, userId, item1.getId());
    }
}