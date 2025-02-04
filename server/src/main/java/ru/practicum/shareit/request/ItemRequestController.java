package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestOutputDto create(@RequestBody RequestInputDto requestInputDto,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.create(requestInputDto, userId);
    }

    @GetMapping
    public List<RequestOutputDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestOutputDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestOutputDto getRequest(@PathVariable Long requestId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return service.getRequest(requestId, userId);
    }

}
