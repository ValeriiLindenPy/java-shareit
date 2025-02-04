package ru.practicum.shareit.request.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.RequestInputDto;
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
    private ItemRequestServiceImpl requestService;

    @Autowired
    private ItemRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requester;
    private User anotherUser;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();
        requestRepository.deleteAll();
        userRepository.deleteAll();

        requester = userRepository.save(User.builder()
                .name("Requester")
                .email("requester@mail.com")
                .build());

        anotherUser = userRepository.save(User.builder()
                .name("Another User")
                .email("another@mail.com")
                .build());
    }

    @Test
    void whenCreateRequest_thenReturnRequestOutputDto() {
        RequestInputDto inputDto = RequestInputDto.builder()
                .description("Нужна дрель")
                .build();

        RequestOutputDto outputDto = requestService.create(inputDto, requester.getId());

        assertNotNull(outputDto);
        assertNotNull(outputDto.getId());
        assertEquals("Нужна дрель", outputDto.getDescription());
        assertNotNull(outputDto.getItems());
        assertTrue(outputDto.getItems().isEmpty());
        assertNotNull(outputDto.getCreated());
    }

    @Test
    void whenCreateRequestWithNonExistingUser_thenThrowNotFoundException() {
        RequestInputDto inputDto = RequestInputDto.builder()
                .description("Нужна отвертка")
                .build();

        long nonExistingUserId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.create(inputDto, nonExistingUserId));
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistingUserId)));
    }

    @Test
    void whenGetRequestsForUserWithoutRequests_thenReturnEmptyList() {
        List<RequestOutputDto> requests = requestService.getRequests(anotherUser.getId());
        assertNotNull(requests);
        assertTrue(requests.isEmpty());
    }

    @Test
    void whenGetRequests_thenReturnListOfRequestsWithItems() {
        RequestInputDto inputDto = RequestInputDto.builder()
                .description("Нужна пила")
                .build();
        RequestOutputDto createdRequest = requestService.create(inputDto, requester.getId());

        Item item = Item.builder()
                .name("Пила")
                .description("Хорошая пила")
                .available(true)
                .request(createdRequest.getId() != null ? requestRepository.findById(createdRequest.getId()).orElse(null) : null)
                .owner(anotherUser)
                .build();
        itemRepository.save(item);

        List<RequestOutputDto> requests = requestService.getRequests(requester.getId());
        assertNotNull(requests);
        assertEquals(1, requests.size());
        RequestOutputDto dto = requests.get(0);
        assertEquals("Нужна пила", dto.getDescription());
        List<ItemDto> items = dto.getItems();
        assertNotNull(items);
        assertFalse(items.isEmpty());
        assertTrue(items.stream().anyMatch(i -> i.getName().equals("Пила")));
    }

    @Test
    void whenGetAllRequests_thenReturnListOfRequests() {
        RequestInputDto inputDto1 = RequestInputDto.builder()
                .description("Запрос 1")
                .build();
        RequestOutputDto req1 = requestService.create(inputDto1, requester.getId());

        RequestInputDto inputDto2 = RequestInputDto.builder()
                .description("Запрос 2")
                .build();
        RequestOutputDto req2 = requestService.create(inputDto2, requester.getId());

        List<RequestOutputDto> requests = requestService.getAllRequests(requester.getId());
        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Запрос 1")));
        assertTrue(requests.stream().anyMatch(r -> r.getDescription().equals("Запрос 2")));
    }

    @Test
    void whenGetRequestById_thenReturnRequestOutputDto() {
        RequestInputDto inputDto = RequestInputDto.builder()
                .description("Запрос для получения")
                .build();
        RequestOutputDto created = requestService.create(inputDto, requester.getId());

        ItemRequest request = requestRepository.findById(created.getId()).orElseThrow();
        Item item = Item.builder()
                .name("Молоток")
                .description("Качественный молоток")
                .available(true)
                .request(request)
                .owner(anotherUser)
                .build();
        itemRepository.save(item);

        RequestOutputDto outputDto = requestService.getRequest(created.getId(), requester.getId());
        assertNotNull(outputDto);
        assertEquals(created.getId(), outputDto.getId());
        assertEquals("Запрос для получения", outputDto.getDescription());
        assertNotNull(outputDto.getItems());
        assertFalse(outputDto.getItems().isEmpty());
        assertTrue(outputDto.getItems().stream().anyMatch(i -> i.getName().equals("Молоток")));
    }

    @Test
    void whenGetNonExistingRequestById_thenThrowNotFoundException() {
        long nonExistingRequestId = 999L;
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> requestService.getRequest(nonExistingRequestId, requester.getId()));
        assertTrue(exception.getMessage().contains(String.valueOf(nonExistingRequestId)));
    }
}
