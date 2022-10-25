package ru.practicum.ewm.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.UserRepository;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.compilations.CompilationRepository;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.EventDtoOutShort;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.statistics.StatisticClient;
import ru.practicum.ewm.statistics.model.ViewStats;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CompilationRepository compilationRepository;
    private final StatisticClient statisticClient;

    public User getUserInDb(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%s was not found.", userId)));
    }

    public Category getCategoryInDb(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%s was not found.", catId)));
    }

    public Pageable getPagination(Integer from, Integer size, String sortField) {
        if ((from < 0 || size < 0) || (size == 0)) {
            throw new RuntimeException("Bad request with pagination parameters.");
        }
        if (sortField != null) {
            return PageRequest.of(from / size, size, Sort.by(sortField).descending());
        } else {
            return PageRequest.of(from / size, size);
        }
    }

    public Event getEventInDb(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%s was not found.", eventId)));
    }

    public EventDtoOutFull addViewsToEventFull(Event event) {
        EventDtoOutFull eventDtoOutFull = EventMapper.INSTANCE.toEventDtoOutFullFromEvent(event);
        eventDtoOutFull.setViews(statisticClient.getViewOfEvent(
                        event.getCreatedOn(), event.getEventDate(), null, null)
                .stream().map(ViewStats::getHits).mapToLong(Long::longValue).sum());
        return eventDtoOutFull;
    }

    public EventDtoOutShort addViewsToEventShort(Event event) {
        EventDtoOutShort eventDtoOutShort = EventMapper.INSTANCE.toEventDtoOutShortFromEvent(event);
        eventDtoOutShort.setViews(statisticClient.getViewOfEvent(
                        event.getCreatedOn(), event.getEventDate(), null, null)
                .stream().map(ViewStats::getHits).mapToLong(Long::longValue).sum());
        return eventDtoOutShort;
    }

    public Request getRequestInDb(Long reqId) {
        return requestRepository.findById(reqId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%s was not found.", reqId)));
    }

    public Compilation getCompilationInDb(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException(
                String.format("Event with id=%s was not found.", compId)));
    }
}
