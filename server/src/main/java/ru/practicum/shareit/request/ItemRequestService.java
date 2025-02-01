package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestInputDto;
import ru.practicum.shareit.request.dto.RequestOutputDto;
import java.util.List;


public interface ItemRequestService {
    RequestOutputDto create(RequestInputDto requestInputDto, Long userId);

    List<RequestOutputDto> getRequests(Long userId);

    List<RequestOutputDto> getAllRequests(Long userId);

    RequestOutputDto getRequest(Long requestId, Long userId);
}
