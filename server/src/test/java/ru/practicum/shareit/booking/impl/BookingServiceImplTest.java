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
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
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

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build();

        booker = User.builder()
                .email("test2@mail.com")
                .name("Booker")
                .build();

        item1 = Item.builder()
                .owner(user)
                .name("item1")
                .description("description1")
                .available(true)
                .build();
    }

    @Test
    void whenGetBookingsWithStatusALL_thenReturnCorrectBookings() {
        LocalDateTime now = LocalDateTime.now().minusSeconds(3);

        userRepository.saveAll(List.of(user, booker));

        Item item2 = Item.builder()
                .owner(user)
                .name("item2")
                .description("description2")
                .available(true)
                .build();

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

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
        assertTrue(bookings.stream().anyMatch(b -> b.getItem().getName().equals("item2")));
    }
}