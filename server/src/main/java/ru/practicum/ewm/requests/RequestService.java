package ru.practicum.ewm.requests;

import ru.practicum.ewm.requests.dto.RequestDto;

import java.util.Collection;

public interface RequestService {
    Collection<RequestDto> getRequestsOfUser(Long userId);

    RequestDto createRequestForUser(Long userId, Long eventId);

    RequestDto cancelRequestByUser(Long userId, Long requestId);
}
