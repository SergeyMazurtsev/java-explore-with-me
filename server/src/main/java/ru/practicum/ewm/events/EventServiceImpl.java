package ru.practicum.ewm.events;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoInPatch;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.EventDtoOutShort;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.IntegrityViolationException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CommonService commonService;
    private final RequestRepository requestRepository;

    @Override
    public Collection<EventDtoOutShort> getEventsUser(Long userId, Integer from, Integer size) {
        User user = commonService.getUserInDb(userId);
        return eventRepository.findAllByInitiator(user, commonService.getPagination(from, size, null))
                .stream()
                .map(commonService::addViewsToEventShort)
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoOutFull patchEvent(Long userId, EventDtoInPatch eventDtoInPatch) {
        Event event = commonService.getEventInDb(eventDtoInPatch.getEventId());
        User user = commonService.getUserInDb(userId);
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException(String.format("Event with id=%s was not found.",
                    eventDtoInPatch.getEventId()));
        }
        if (event.getState().equals(EventState.CANCELED)) {
            event.setRequestModeration(true);
        }
        if (LocalDateTime.now().plusHours(2).isAfter(event.getEventDate())) {
            throw new ValidationException("The change cannot be later than 2 hours before the event.");
        }
        EventMapper.INSTANCE.patchingEvent(eventDtoInPatch, event);
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Patch event method", "event category"));
        }
    }

    @Override
    public EventDtoOutFull createEvent(Long userId, EventDtoIn eventDtoIn) {
        User user = commonService.getUserInDb(userId);
        Event event = EventMapper.INSTANCE.toEventFromEventDtoIn(eventDtoIn);
        Category category = commonService.getCategoryInDb(eventDtoIn.getCategory());
        event.setCategoryId(category);
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        if (LocalDateTime.now().plusHours(2).isAfter(event.getEventDate())) {
            throw new ValidationException("The posting event cannot be later than 2 hours before the event.");
        }
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Save event method", "event category"));
        }
    }

    @Override
    public EventDtoOutFull getEventOfUser(Long userId, Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        User user = commonService.getUserInDb(userId);
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException(String.format("Event with id=%s was not found.",
                    eventId));
        }
        return commonService.addViewsToEventFull(event);
    }

    @Override
    public EventDtoOutFull cancelEventOfUser(Long userId, Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        User user = commonService.getUserInDb(userId);
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException(String.format("Event with id=%s was not found.",
                    eventId));
        }
        event.setState(EventState.CANCELED);
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Cancel event method", "event category"));
        }
    }


    @Override
    public Collection<RequestDto> getRequestsInEventOfUser(Long userId, Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        User user = commonService.getUserInDb(userId);
        if (!event.getInitiator().equals(user)) {
            throw new ValidationException("User must be event initiator.");
        }
        return requestRepository.findAllByEventId(event)
                .stream()
                .map(RequestMapper.INSTANCE::toRequestDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto patchRequestInEventOfUserConfirm(Long userId, Long eventId, Long reqId) {
        Event event = commonService.getEventInDb(eventId);
        commonService.getUserInDb(userId);
        Request request = commonService.getRequestInDb(reqId);
        if (event.getParticipantLimit() <= event.getRequests()
                .stream().filter(i -> i.getStatus().equals(EventState.CONFIRMED))
                .collect(Collectors.toList())
                .size()) {
            throw new ValidationException("Limit of requests is rich.");
        }
        if (event.getParticipantLimit() == null || event.getParticipantLimit() == 0
                || event.getRequestModeration().equals(false)) {
            request.setStatus(EventState.CONFIRMED);
        }
        if (event.getParticipantLimit() == event.getRequests()
                .stream().filter(i -> i.getStatus().equals(EventState.CONFIRMED))
                .collect(Collectors.toList())
                .size() + 1) {
            Collection<Request> requests = requestRepository.findAllByEventId(event)
                    .stream().filter(i -> i.getStatus().equals(EventState.PENDING))
                    .collect(Collectors.toList());
            if (requests.size() > 0) {
                requests = requests.stream().peek(i -> i.setStatus(EventState.CANCELED)).collect(Collectors.toList());
                requests.stream().peek(requestRepository::save);
            }
        }
        request.setStatus(EventState.CONFIRMED);
        try {
            return RequestMapper.INSTANCE.toRequestDtoFromRequest(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Confirm event request method", "event category"));
        }
    }

    @Override
    public RequestDto patchRequestInEventOfUserReject(Long userId, Long eventId, Long reqId) {
        commonService.getEventInDb(eventId);
        commonService.getUserInDb(userId);
        Request request = commonService.getRequestInDb(reqId);
        request.setStatus(EventState.REJECTED);
        try {
            return RequestMapper.INSTANCE.toRequestDtoFromRequest(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Confirm event request method", "event category"));
        }
    }


    @Override
    public Collection<EventDtoOutShort> getPublicEvents(@Nullable String text, @Nullable List<Long> categories,
                                                        @Nullable Boolean paid, @Nullable LocalDateTime rangeStart,
                                                        @Nullable LocalDateTime rangeEnd, @Nullable Boolean onlyAvailable,
                                                        @Nullable EventPublicSort sort, Integer from, Integer size) {
        List<Event> events = new ArrayList<>();
        if (text != null) {
            if (paid != null) {
                val elements = eventRepository.findAllByStateAndAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCaseAndAndPaid(
                        EventState.PUBLISHED, text, text, text, paid);
                if (!elements.isEmpty()) {
                    events.addAll(elements);
                }
            } else {
                val elements = eventRepository.findAllByStateAndAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
                        EventState.PUBLISHED, text, text, text);
                if (!elements.isEmpty()) {
                    events.addAll(elements);
                }
            }
        } else if (paid != null) {
            val elements = eventRepository.findAllByPaidAndState(paid, EventState.PUBLISHED);
            if (!elements.isEmpty()) {
                events.addAll(elements);
            }
        } else {
            events = eventRepository.findAllByState(EventState.PUBLISHED);
        }
        events = commonService.filterEventsByCategory(events, categories);
        events = commonService.filterEventsByRangeStart(events, rangeStart);
        events = commonService.filterEventsByRangeEnd(events, rangeEnd);
        if (onlyAvailable != null) {
            events = events.stream().filter(element -> element.getRequests().size() < element.getParticipantLimit())
                    .collect(Collectors.toList());
        }
        List<EventDtoOutShort> result = new PageImpl<>(events.stream()
                .map(commonService::addViewsToEventShort)
                .collect(Collectors.toList()), commonService.getPagination(from, size, null), events.size())
                .stream().collect(Collectors.toList());
        if (sort != null) {
            switch (sort) {
                case EVENT_DATE:
                    result = result.stream().sorted(Comparator.comparing(EventDtoOutShort::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case VIEWS:
                    result = result.stream().sorted(Comparator.comparing(EventDtoOutShort::getViews))
                            .collect(Collectors.toList());
                    break;
            }
        }
        return result;
    }

    @Override
    public EventDtoOutFull getPublicEvent(Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("Event with id = %s not found.", eventId));
        }
        return commonService.addViewsToEventFull(event);
    }
}
