package ru.practicum.shareit.request.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public RequestOutputDto create(RequestInputDto requestInputDto, Long userId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found".formatted(userId)));

        ItemRequest request = ItemRequest.builder()
                .description(requestInputDto.getDescription())
                .requester(requester)
                .build();
        ItemRequest savedRequest = repository.save(request);
        return RequestMapper.mapToOutputDto(savedRequest, Collections.emptyList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestOutputDto> getRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found".formatted(userId)));

        List<ItemRequest> requests = repository.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<ItemDto> items = itemRepository.findByRequest_Id(request.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return RequestMapper.mapToOutputDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestOutputDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id - %d not found".formatted(userId)));

        List<ItemRequest> requests = repository.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(request -> {
                    List<ItemDto> items = itemRepository.findByRequest_Id(request.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList());
                    return RequestMapper.mapToOutputDto(request, items);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RequestOutputDto getRequest(Long requestId, Long userId) {
        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id - %d not found".formatted(requestId)));

        List<ItemDto> items = itemRepository.findByRequest_Id(requestId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return RequestMapper.mapToOutputDto(request, items);
    }
}
