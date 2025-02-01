package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.error.ValidationMarker;
import ru.practicum.shareit.request.dto.RequestInputDto;



@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(ValidationMarker.OnCreate.class)
                                         @RequestBody RequestInputDto requestInputDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.createRequest(requestInputDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@PathVariable Long requestId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getRequest(requestId, userId);
    }

}
