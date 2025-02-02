package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper mapper;

    private Long userId = 1L;
    private Long bookingId = 1L;
    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;

    @BeforeEach
    void setUp() {
        mapper = mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(10L)
                .start(LocalDateTime.of(2024, 1, 10, 10, 0))
                .end(LocalDateTime.of(2024, 1, 15, 10, 0))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(bookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(ItemDto.builder()
                        .id(10L)
                        .name("Test Item")
                        .description("Test Description")
                        .available(true)
                        .build())
                .booker(UserDto.builder()
                        .id(userId)
                        .name("UserName")
                        .email("user@email")
                        .build())
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(BookingRequestDto.class), eq(userId)))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(mapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(10L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).create(any(BookingRequestDto.class), eq(userId));
    }

    @Test
    void setApprove() throws Exception {
        BookingResponseDto approvedResponse = BookingResponseDto.builder()
                .id(bookingId)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(bookingResponseDto.getItem())
                .booker(bookingResponseDto.getBooker())
                .status(BookingStatus.APPROVED)
                .build();

        Boolean approved = true;
        when(bookingService.setApprove(bookingId, userId, approved))
                .thenReturn(approvedResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", approved.toString())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));

        verify(bookingService, times(1)).setApprove(bookingId, userId, approved);
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(bookingId, userId))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.id").value(10L))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"));

        verify(bookingService, times(1)).getBooking(bookingId, userId);
    }

    @Test
    void getBookings() throws Exception {
        BookingResponseDto bookingResponse2 = BookingResponseDto.builder()
                .id(2L)
                .start(LocalDateTime.of(2023, 2, 10, 10, 0))
                .end(LocalDateTime.of(2023, 2, 12, 10, 0))
                .item(ItemDto.builder().id(5L).name("Item2").build())
                .booker(bookingResponseDto.getBooker())
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.getBookings(userId, BookingState.ALL))
                .thenReturn(List.of(bookingResponseDto, bookingResponse2));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[1].id").value(2L));

        verify(bookingService, times(1)).getBookings(userId, BookingState.ALL);
    }

    @Test
    void getBookingsByOwnerItems() throws Exception {
        when(bookingService.getBookingsByOwnerItems(userId, BookingState.ALL))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(bookingId))
                .andExpect(jsonPath("$[0].item.id").value(10L));

        verify(bookingService, times(1)).getBookingsByOwnerItems(userId, BookingState.ALL);
    }
}
