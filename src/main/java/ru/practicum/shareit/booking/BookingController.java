package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;


import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.create(bookingRequestDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto setApprove(@PathVariable Long bookingId,
                                         @RequestParam(value = "approved") Boolean approved,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.setApprove(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {

        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state", required = false, defaultValue = "ALL") BookingState state) {

        return bookingService.getBookingsByOwnerItems(userId, state);
    }
}
