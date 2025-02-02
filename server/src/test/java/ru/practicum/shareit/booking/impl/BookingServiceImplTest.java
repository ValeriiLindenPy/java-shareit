package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
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

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private User booker;
    private Item item1;
    private Item item2;
    private LocalDateTime now;

    @BeforeEach
    void setup() {
        now = LocalDateTime.now();
        // Сначала сохраняем пользователей
        user = userRepository.save(User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build());

        booker = userRepository.save(User.builder()
                .email("test2@mail.com")
                .name("Booker")
                .build());

        // Сначала сохраняем item1, чтобы он стал persistent
        item1 = itemRepository.save(Item.builder()
                .owner(user)
                .name("item1")
                .description("description1")
                .available(true)
                .build());

        // Сохраняем второй item
        item2 = itemRepository.save(Item.builder()
                .owner(user)
                .name("Item2")
                .description("Desc2")
                .available(true)
                .build());

        // Создаем бронирования. Теперь item1 и item2 уже сохранены.
        Booking pastBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(now.minusDays(5))
                .end(now.minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        Booking currentBooking = Booking.builder()
                .item(item2)
                .booker(booker)
                .start(now.minusHours(1))
                .end(now.plusHours(1))
                .status(BookingStatus.APPROVED)
                .build();

        Booking futureBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        Booking rejectedBooking = Booking.builder()
                .item(item2)
                .booker(booker)
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .status(BookingStatus.REJECTED)
                .build();

        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));
    }

    @Test
    void whenGetBookingsWithStatusALL_thenReturnCorrectBookings() {
        now = now.minusSeconds(3);

        bookingRepository.deleteAll();


        Booking booking = Booking.builder()
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item1)
                .start(now.plusSeconds(1))
                .end(now.plusSeconds(2))
                .build();

        Booking booking2 = Booking.builder()
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item2)
                .start(now.plusSeconds(10))
                .end(now.plusSeconds(12))
                .build();

        bookingRepository.saveAll(List.of(booking, booking2));

        List<BookingResponseDto> bookings = bookingService.getBookings(booker.getId(), BookingState.ALL);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().anyMatch(b -> b.getItem().getName().equals(item1.getName())));
        assertTrue(bookings.stream().anyMatch(b -> b.getItem().getName().equals(item2.getName())));
    }

    @Test
    void whenOwnerApprovesBooking_thenStatusChangesToApproved() {

        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        BookingResponseDto response = bookingService.setApprove(booking.getId(), user.getId(), true);

        assertNotNull(response);
        assertEquals(BookingStatus.APPROVED, response.getStatus());
    }

    @Test
    void whenOwnerRejectsBooking_thenStatusChangesToRejected() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        BookingResponseDto response = bookingService.setApprove(booking.getId(), user.getId(), false);

        assertNotNull(response);
        assertEquals(BookingStatus.REJECTED, response.getStatus());
    }

    @Test
    void whenNonOwnerTriesToApproveBooking_thenThrowOwnerException() {

        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.WAITING)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        assertThrows(OwnerException.class, () ->
                bookingService.setApprove(booking.getId(), booker.getId(), true)
        );
    }

    @Test
    void whenTryingToApproveNonWaitingBooking_thenThrowBookingException() {
        Booking booking = bookingRepository.save(Booking.builder()
                .booker(booker)
                .item(item1)
                .status(BookingStatus.APPROVED)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build());

        assertThrows(BookingException.class, () ->
                bookingService.setApprove(booking.getId(), user.getId(), true)
        );
    }

    @Test
    void whenTryingToApproveNonExistingBooking_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () ->
                bookingService.setApprove(999L, user.getId(), true) // 999L — ID несуществующего бронирования
        );
    }

    @Test
    void whenGetBookingsByOwnerItemsWithNonExistingUser_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwnerItems(999L, BookingState.ALL));
    }

    @Test
    void whenGetBookingsByOwnerItemsWithoutOwnedItems_thenThrowOwnerException() {
        User otherUser = User.builder()
                .email("other@mail.com")
                .name("Other User")
                .build();

        userRepository.save(otherUser);

        assertThrows(OwnerException.class, () -> bookingService.getBookingsByOwnerItems(otherUser.getId(), BookingState.ALL));
    }

    @Test
    void whenGetBookingsByNonExistingUser_thenThrowNotFoundException() {
        Long nonExistingUserId = 9999L;
        assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookings(nonExistingUserId, BookingState.ALL)
        );
    }

    @Test
    void whenGetBookingsWithStatusCURRENT_thenReturnOnlyCurrentBooking() {
        List<BookingResponseDto> bookings = bookingService.getBookings(booker.getId(), BookingState.CURRENT);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingResponseDto current = bookings.get(0);
        LocalDateTime nowTime = LocalDateTime.now();
        assertTrue(current.getStart().isBefore(nowTime) || current.getStart().isEqual(nowTime));
        assertTrue(current.getEnd().isAfter(nowTime) || current.getEnd().isEqual(nowTime));
        assertEquals(item2.getId(), current.getItem().getId());
    }

    @Test
    void whenGetBookingsWithStatusPAST_thenReturnOnlyPastBooking() {
        List<BookingResponseDto> bookings = bookingService.getBookings(booker.getId(), BookingState.PAST);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingResponseDto past = bookings.get(0);
        assertTrue(past.getEnd().isBefore(LocalDateTime.now()));
        assertEquals(item1.getId(), past.getItem().getId());
    }

    @Test
    void whenGetBookingsWithStatusFUTURE_thenReturnOnlyFutureBookings() {
        List<BookingResponseDto> bookings = bookingService.getBookings(booker.getId(), BookingState.FUTURE);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        LocalDateTime currentTime = LocalDateTime.now();
        for (BookingResponseDto dto : bookings) {
            assertTrue(dto.getStart().isAfter(currentTime));
        }
    }

    @Test
    void whenGetBookingsWithStatusWAITING_thenReturnOnlyWaitingBookings() {
        List<BookingResponseDto> bookings = bookingService.getBookings(booker.getId(), BookingState.WAITING);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingResponseDto waiting = bookings.get(0);
        assertEquals(BookingStatus.WAITING, waiting.getStatus());
    }

    @Test
    void whenGetBookingsWithStatusREJECTED_thenReturnOnlyRejectedBookings() {
        List<BookingResponseDto> bookings = bookingService.getBookings(booker.getId(), BookingState.REJECTED);
        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        BookingResponseDto rejected = bookings.get(0);
        assertEquals(BookingStatus.REJECTED, rejected.getStatus());
    }

    @Test
    void whenCreateBookingSuccessfully_thenReturnBookingResponseDto() {
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(2).plusSeconds(1))
                .end(now.plusDays(3))
                .build();

        BookingResponseDto response = bookingService.create(requestDto, booker.getId());

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(requestDto.getStart(), response.getStart());
        assertEquals(requestDto.getEnd(), response.getEnd());
        assertEquals(BookingStatus.WAITING, response.getStatus());
        assertEquals(item1.getId(), response.getItem().getId());
        assertEquals(booker.getId(), response.getBooker().getId());
    }


    @Test
    void whenCreateBookingWithNonExistingUser_thenThrowNotFoundException() {
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        Long nonExistingUserId = 9999L;

        assertThrows(NotFoundException.class, () ->
                bookingService.create(requestDto, nonExistingUserId)
        );
    }

    @Test
    void whenCreateBookingWithNonExistingItem_thenThrowNotFoundException() {

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(9999L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () ->
                bookingService.create(requestDto, booker.getId())
        );
    }

    @Test
    void whenCreateBookingForUnavailableItem_thenThrowUnavailableItemException() {
        item1.setAvailable(false);
        itemRepository.save(item1);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        assertThrows(UnavailableItemException.class, () ->
                bookingService.create(requestDto, booker.getId())
        );
    }

    @Test
    void whenCreateBookingWithStartAfterEnd_thenThrowDateValidationException() {
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(2))
                .end(now.plusDays(1))
                .build();

        assertThrows(DateValidationException.class, () ->
                bookingService.create(requestDto, booker.getId())
        );
    }

    @Test
    void whenCreateBookingOverlappingWithExistingBooking_thenThrowBookingOverlapException() {
        var existingBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .start(now.plusDays(3))
                .end(now.plusDays(4))
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(existingBooking);

        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(item1.getId())
                .start(now.plusDays(3).plusHours(1))
                .end(now.plusDays(4).plusHours(1))
                .build();

        assertThrows(BookingOverlapException.class, () ->
                bookingService.create(requestDto, booker.getId())
        );
    }
}