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
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.comment.CommentRequestDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
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
    void whenGetUserItems_thenReturnCorrectItems() {

        user = userRepository.save(user);

        Item item2 = Item.builder()
                .owner(user)
                .name("item2")
                .description("description2")
                .available(true)
                .build();

        itemRepository.saveAll(List.of(item1, item2));

        List<Item> items = itemService.getAll(user.getId())
                .stream().map(ItemMapper::toItem).toList();

        assertNotNull(items);
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item1.getName())));
        assertTrue(items.stream().anyMatch(i -> i.getName().equals(item2.getName())));
    }

    @Test
    void whenCreateComment_thenReturnCorrectCommentRespondDto() {

        user = userRepository.save(user);
        booker = userRepository.save(booker);
        item1 = itemRepository.save(item1);


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
}