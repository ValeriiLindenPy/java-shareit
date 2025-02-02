package ru.practicum.shareit.request.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.RequestOutputDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ItemRequestServiceImplTest {

    @Autowired
    private ItemRequestService requestService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository requestRepository;

    private User user;
    private User requester;
    private Item item1;
    private ItemRequest request;

    @BeforeEach
    void setup() {
        user = User.builder()
                .email("test@mail.com")
                .name("Sam")
                .build();

        requester = User.builder()
                .email("test2@mail.com")
                .name("Requester")
                .build();

        user = userRepository.save(user);
        requester = userRepository.save(requester);

        request = ItemRequest.builder()
                .description("Item1")
                .requester(requester)
                .build();

        request = requestRepository.save(request);

        item1 = Item.builder()
                .owner(user)
                .name("item1")
                .description("description1")
                .request(request)
                .available(true)
                .build();
    }

    @Test
    void whenGetAllRequests_ThenReturnCorrectRequests() {
        List<RequestOutputDto> requests = requestService.getAllRequests(requester.getId());
        assertNotNull(requests);
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals(request.getDescription())));
    }
}