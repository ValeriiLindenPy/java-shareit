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
import java.util.Objects;

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

        boolean isItemAvailable = bookingRepository.findByItemAndTimeRange(
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

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return BookingMapper.toResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {


        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id - %d not found".formatted(bookingId))
        );

        if (!Objects.equals(booking.getBooker().getId(), userId) &&
                !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new OwnerException("You are not the owner or booker of the item.");
        }

        return BookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookings(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                );

        return switch (state) {
            case ALL -> bookingRepository.findAllByBookerId(userId).stream().map(BookingMapper::toResponseDto).toList();
            case CURRENT ->
                    bookingRepository.findCurrentBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case PAST ->
                    bookingRepository.findPastBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case FUTURE ->
                    bookingRepository.findFutureBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case WAITING ->
                    bookingRepository.findWaitingBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case REJECTED ->
                    bookingRepository.findRejectedBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
        };
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwnerItems(Long userId, BookingState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found"
                        .formatted(userId))
                );

        itemRepository.findItemsByOwnerId(userId).stream().findFirst().orElseThrow(
                () -> new OwnerException("You are not owner of any item.")
        );

        return switch (state) {
            case ALL -> bookingRepository.findAllByBookerId(userId).stream().map(BookingMapper::toResponseDto).toList();
            case CURRENT ->
                    bookingRepository.findCurrentBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case PAST ->
                    bookingRepository.findPastBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case FUTURE ->
                    bookingRepository.findFutureBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case WAITING ->
                    bookingRepository.findWaitingBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
            case REJECTED ->
                    bookingRepository.findRejectedBookings(userId, LocalDateTime.now()).stream().map(BookingMapper::toResponseDto).toList();
        };
    }
}
