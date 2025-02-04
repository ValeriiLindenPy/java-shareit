package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestBody BookingRequestDto bookingRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking {}, userId={}", bookingRequestDto.toString(), userId);
        BookingResponseDto response = bookingService.create(bookingRequestDto, userId);
        log.info("Response booking created - {}", response.toString());
        return response;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto setApprove(@PathVariable Long bookingId,
                                         @RequestParam(value = "approved") Boolean approved,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("approve: bookingId -{}, approved -{}, userid - {}", bookingId,approved, userId);
        return bookingService.setApprove(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
        log.info("Get bookings with state {}, userId={}", state, userId);
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {
        log.info("getBookingsByOwnerItems: userId - {}, stateParam - {}", userId, state);
        return bookingService.getBookingsByOwnerItems(userId, state);
    }
}
