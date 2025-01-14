package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(BookingRequestDto bookingRequestDto, Long userId);

    BookingResponseDto setApprove(Long bookingId, Long userId, Boolean approved);


    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getBookings(Long userId, BookingState state);

    List<BookingResponseDto> getBookingsByOwnerItems(Long userId, BookingState state);
}
