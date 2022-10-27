package ru.practicum.ewm.events;

import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoInPatch;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.EventDtoOutShort;
import ru.practicum.ewm.requests.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    Collection<EventDtoOutShort> getEventsUser(Long userId, Integer from, Integer size);

    EventDtoOutFull patchEvent(Long userId, EventDtoInPatch eventDtoInPatch);

    EventDtoOutFull createEvent(Long userId, EventDtoIn eventDtoIn);

    EventDtoOutFull getEventOfUser(Long userId, Long eventId);

    EventDtoOutFull cancelEventOfUser(Long userId, Long eventId);

    Collection<RequestDto> getRequestsInEventOfUser(Long userId, Long eventId);

    RequestDto patchRequestInEventOfUserConfirm(Long userId, Long eventId, Long reqId);

    RequestDto patchRequestInEventOfUserReject(Long userId, Long eventId, Long reqId);

    Collection<EventDtoOutShort> getPublicEvents(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Boolean onlyAvailable,
                                                 EventPublicSort sort,
                                                 Integer from,
                                                 Integer size);

    EventDtoOutFull getPublicEvent(Long eventId);
}
