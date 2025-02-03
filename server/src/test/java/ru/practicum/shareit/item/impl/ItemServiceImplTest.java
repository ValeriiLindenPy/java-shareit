package ru.practicum.shareit.item.impl;

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
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.exception.BookingException;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.error.exception.OwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemServiceImpl itemService;
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

    @BeforeEach
    void setup() {
        user = userRepository.save(User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build());

        booker = userRepository.save(User.builder()
                .email("test2@mail.com")
                .name("Booker")
                .build());

        item1 = itemRepository.save(Item.builder()
                .owner(user)
                .name("item1")
                .description("description1")
                .available(true)
                .build());

        item2 = itemRepository.save(Item.builder()
                .owner(user)
                .name("Item2")
                .description("Desc2")
                .available(true)
                .build());
    }

    @Test
    void whenGetUserItems_thenReturnCorrectItems() {
        List<ItemDto> items = itemService.getAll(user.getId());
        assertNotNull(items);
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item1.getName())));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item2.getName())));
    }

    @Test
    void whenCreateComment_thenReturnCorrectCommentRespondDto() {
        LocalDateTime now = LocalDateTime.now().minusSeconds(3);

        Booking booking = Booking.builder()
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .item(item1)
                .start(now.plusSeconds(1))
                .end(now.plusSeconds(2))
                .build();

        bookingRepository.save(booking);

        CommentRequestDto comment = CommentRequestDto.builder()
                .text("Comment")
                .build();

        CommentResponseDto responseDto = itemService.createComment(comment,booker.getId(), item1.getId());

        assertNotNull(responseDto);
        assertEquals(responseDto.getAuthorName(), booker.getName());
        assertEquals(responseDto.getText(), comment.getText());
    }

    @Test
    void whenEditItem_thenReturnEditedItemDto() {
        ItemDto update = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .available(false)
                .build();

        ItemDto updatedDto = itemService.editOne(item1.getId(), update, user.getId());

        assertNotNull(updatedDto);
        assertEquals("UpdatedName", updatedDto.getName());
        assertEquals("UpdatedDescription", updatedDto.getDescription());
        assertFalse(updatedDto.getAvailable());
    }

    @Test
    void whenEditItemWithNonOwner_thenThrowOwnerException() {
        ItemDto update = ItemDto.builder()
                .name("NewName")
                .build();

        assertThrows(OwnerException.class, () -> itemService.editOne(item1.getId(), update, booker.getId()));
    }

    @Test
    void whenEditItemWithNonItem_thenThrowNotFoundException() {
        ItemDto update = ItemDto.builder()
                .name("NewName")
                .build();

        assertThrows(NotFoundException.class, () -> itemService.editOne(999L, update, booker.getId()));
    }

    @Test
    void whenEditItemWithNonUser_thenThrowNotFoundException() {
        ItemDto update = ItemDto.builder()
                .name("NewName")
                .build();

        assertThrows(NotFoundException.class, () -> itemService.editOne(item1.getId(), update,999L));
    }

    @Test
    void whenSearchByText_thenReturnMatchingItems() {
        List<ItemDto> result = itemService.searchByText("item1");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1.getName(), result.getFirst().getName());
    }

    @Test
    void whenSearchByEmptyText_thenReturnEmptyList() {
        List<ItemDto> result = itemService.searchByText("   ");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenCreateItem_thenReturnCreatedItemDto() {
        ItemDto newItemDto = ItemDto.builder()
                .name("NewItem")
                .description("NewDescription")
                .available(true)
                .build();

        ItemDto created = itemService.create(newItemDto, user.getId());

        assertNotNull(created);
        assertEquals("NewItem", created.getName());
        assertEquals("NewDescription", created.getDescription());
        assertTrue(created.getAvailable());
    }

    @Test
    void whenGetByIdAndOwnerId_thenReturnItemOwnerDtoWithBookingsAndComments() {
        LocalDateTime now = LocalDateTime.now();

        Booking pastBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(now.minusDays(3))
                .end(now.minusDays(2))
                .build();
        bookingRepository.save(pastBooking);

        Booking futureBooking = Booking.builder()
                .item(item1)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
        bookingRepository.save(futureBooking);

        CommentRequestDto commentRequest = CommentRequestDto.builder()
                .text("Nice item")
                .build();
        CommentResponseDto commentResponse = itemService.createComment(commentRequest, booker.getId(), item1.getId());
        assertNotNull(commentResponse);

        ItemOwnerDto ownerDto = itemService.getByIdAndOwnerId(item1.getId(), user.getId());
        assertNotNull(ownerDto);

        assertEquals(pastBooking.getEnd(), ownerDto.getLastBooking());
        assertEquals(futureBooking.getStart(), ownerDto.getNextBooking());

        assertNotNull(ownerDto.getComments());
        assertFalse(ownerDto.getComments().isEmpty());
        assertTrue(ownerDto.getComments().stream().anyMatch(c -> c.getText().equals("Nice item")));
    }

    @Test
    void whenGetByIdAndOwnerIdWithNonExistingItem_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.getByIdAndOwnerId(999L, user.getId()));
    }

    @Test
    void whenCreateCommentWithoutBooking_thenThrowBookingException() {
        CommentRequestDto commentRequest = CommentRequestDto.builder()
                .text("Should fail")
                .build();
        assertThrows(BookingException.class, () -> itemService.createComment(commentRequest, booker.getId(), item2.getId()));
    }

    @Test
    void whenGetByIdAndOwnerIdForNonOwner_thenReturnDtoWithoutBookingInfo() {
        CommentRequestDto commentRequest = CommentRequestDto.builder()
                .text("Отличный товар!")
                .build();

        try {
            itemService.createComment(commentRequest, booker.getId(), item1.getId());
        } catch (Exception ignored) {}

        ItemOwnerDto dto = itemService.getByIdAndOwnerId(item1.getId(), booker.getId());

        assertNotNull(dto);
        assertNull(dto.getLastBooking(), "lastBooking должна быть null для не-владельца");
        assertNull(dto.getNextBooking(), "nextBooking должна быть null для не-владельца");

        if (dto.getComments() != null && !dto.getComments().isEmpty()) {
            assertTrue(dto.getComments().stream().anyMatch(c -> c.getText().equals("Отличный товар!")));
        }
    }
}