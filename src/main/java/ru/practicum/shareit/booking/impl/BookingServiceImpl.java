package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(BookingRequestDto bookingRequestDto, Long userId) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                );

        Item item = itemRepository.findById(bookingRequestDto
                .getItemId()).orElseThrow(() -> new NotFoundException("Item with id - %d not found"
                .formatted(bookingRequestDto.getItemId()))
        );

        if (!item.getAvailable()) {
            throw new UnavailableItemException("This item is not available for booking!");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new DateValidationException("Start date must be before end date.Start: " + bookingRequestDto.getStart());
        }

        boolean isItemAvailable = bookingRepository.findOverlappingBookings(
                item.getId(),
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd()
        ).isEmpty();

        if (!isItemAvailable) {
            throw new BookingOverlapException("Item is already booked for the selected period.");
        }

        Booking booking = Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status(BookingStatus.WAITING)
                .item(item)
                .booker(booker)
                .build();

        return BookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto setApprove(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id - %d not found".formatted(bookingId))
        );

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new OwnerException("You are not the owner of the item.");
        }

        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new BookingException("To set booking status it should be WAITING.");
        }

        return BookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBooking(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId).orElseThrow(
                () -> new NotFoundException("Booking with id - %d not found or access denied".formatted(bookingId))
        );

        return BookingMapper.toResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookings(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                );

        return switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId).stream().map(BookingMapper::toResponseDto).toList();
            case CURRENT ->
                    bookingRepository.findCurrentBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case PAST ->
                    bookingRepository.findPastBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case FUTURE ->
                    bookingRepository.findFutureBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case WAITING ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING).stream().map(BookingMapper::toResponseDto).toList();
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED).stream().map(BookingMapper::toResponseDto).toList();
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByOwnerItems(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                );

        itemRepository.findByOwnerId(userId).stream().findFirst().orElseThrow(
                () -> new OwnerException("You are not owner of any item.")
        );

        return switch (state) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId).stream().map(BookingMapper::toResponseDto).toList();
            case CURRENT ->
                    bookingRepository.findCurrentBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case PAST ->
                    bookingRepository.findPastBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case FUTURE ->
                    bookingRepository.findFutureBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case WAITING ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING).stream().map(BookingMapper::toResponseDto).toList();
            case REJECTED ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED).stream().map(BookingMapper::toResponseDto).toList();
        };
    }
}
