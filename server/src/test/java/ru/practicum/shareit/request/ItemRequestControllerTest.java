package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService service;

    private ObjectMapper mapper;

    private Long userId;
    private Long requestId;
    private RequestInputDto requestInputDto;
    private RequestOutputDto requestOutputDto;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        userId = 1L;
        requestId = 2L;

        requestInputDto = RequestInputDto.builder()
                .description("Need a drill")
                .build();

        requestOutputDto = RequestOutputDto.builder()
                .id(requestId)
                .description("Need a drill")
                .items(List.of(
                        ItemDto.builder().id(10L).name("Drill").available(true).build()
                ))
                .created(LocalDateTime.of(2023, 1, 1, 12, 0))
                .build();
    }

    @Test
    void create() throws Exception {
        when(service.create(requestInputDto, userId)).thenReturn(requestOutputDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(requestInputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.items[0].name").value("Drill"));

        verify(service, times(1)).create(requestInputDto, userId);
    }

    @Test
    void getRequests() throws Exception {
        when(service.getRequests(userId)).thenReturn(List.of(requestOutputDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId))
                .andExpect(jsonPath("$[0].description").value("Need a drill"));

        verify(service, times(1)).getRequests(userId);
    }

    @Test
    void getAllRequests() throws Exception {
        when(service.getAllRequests(userId)).thenReturn(List.of(requestOutputDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(requestId));

        verify(service, times(1)).getAllRequests(userId);
    }

    @Test
    void getRequest() throws Exception {
        when(service.getRequest(requestId, userId)).thenReturn(requestOutputDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"));

        verify(service, times(1)).getRequest(requestId, userId);
    }
}
