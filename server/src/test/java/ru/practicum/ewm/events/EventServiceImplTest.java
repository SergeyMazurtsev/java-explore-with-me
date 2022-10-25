package ru.practicum.ewm.events;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.model.Request;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    private final EventRepository eventRepository = Mockito.mock(EventRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    private final EventService eventService = new EventServiceImpl(eventRepository, commonService, requestRepository);
    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private Event event1;
    private Event event2;
    private EventDtoIn eventDtoIn;
    private EventDtoOutShort eventDtoOutShort1;
    private EventDtoOutShort eventDtoOutShort2;
    private EventDtoOutFull eventDtoOutFull1;
    private EventDtoOutFull eventDtoOutFull2;
    private Category category;
    private CategoryDto categoryDto;
    private Pageable pageable;
    private Request request;
    private RequestDto requestDto;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("Test1")
                .email("qwerty@qqq.ru")
                .build();
        user2 = User.builder()
                .id(2L)
                .name("Test2")
                .email("wertyu@www.ru")
                .build();
        userDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();
        userDto2 = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();
        category = Category.builder()
                .id(1L)
                .name("Testing")
                .build();
        categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
        event1 = Event.builder()
                .id(1L)
                .annotation("Testing event1")
                .categoryId(category)
                .createdOn(LocalDateTime.now())
                .description("Testing description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(user1)
                .locationLon(10.10)
                .locationLat(12.1)
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Testing title.")
                .requests(new HashSet<>(Arrays.asList(Request.builder()
                        .id(1L)
                        .status(EventState.PUBLISHED)
                        .requester(user1)
                        .created(LocalDateTime.now())
                        .eventId(event1)
                        .build())))
                .build();
        event2 = Event.builder()
                .id(2L)
                .annotation("Testing event2")
                .categoryId(category)
                .createdOn(LocalDateTime.now())
                .description("Testing description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(user2)
                .locationLon(10.10)
                .locationLat(12.1)
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Testing title.")
                .requests(new HashSet<>(Arrays.asList(request)))
                .build();
        eventDtoOutFull1 = EventDtoOutFull.builder()
                .id(event1.getId())
                .annotation(event1.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event1.getRequests().stream().count())
                .createdOn(event1.getCreatedOn())
                .description(event1.getDescription())
                .eventDate(event1.getEventDate())
                .initiator(userDto1)
                .location(new Location(event1.getLocationLat(), event1.getLocationLon()))
                .paid(event1.getPaid())
                .participantLimit(event1.getParticipantLimit())
                .publishedOn(event1.getPublishedOn())
                .requestModeration(event1.getRequestModeration())
                .state(event1.getState())
                .title(event1.getTitle())
                .views(2L)
                .build();
        eventDtoOutFull2 = EventDtoOutFull.builder()
                .id(event2.getId())
                .annotation(event2.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event2.getRequests().stream().count())
                .createdOn(event2.getCreatedOn())
                .description(event2.getDescription())
                .eventDate(event2.getEventDate())
                .initiator(userDto2)
                .location(new Location(event2.getLocationLat(), event2.getLocationLon()))
                .paid(event2.getPaid())
                .participantLimit(event2.getParticipantLimit())
                .publishedOn(event2.getPublishedOn())
                .requestModeration(event2.getRequestModeration())
                .state(event2.getState())
                .title(event2.getTitle())
                .views(2L)
                .build();
        eventDtoIn = EventDtoIn.builder()
                .id(event1.getId())
                .annotation(event1.getAnnotation())
                .category(event1.getCategoryId().getId())
                .description(event1.getDescription())
                .eventDate(event1.getEventDate())
                .location(new Location(event1.getLocationLat(), event1.getLocationLon()))
                .paid(event1.getPaid())
                .participantLimit(event1.getParticipantLimit())
                .requestModeration(event1.getRequestModeration())
                .title(event1.getTitle())
                .build();
        eventDtoOutShort1 = EventDtoOutShort.builder()
                .id(event1.getId())
                .annotation(event1.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event1.getRequests().stream().count())
                .eventDate(event1.getEventDate())
                .initiator(userDto1)
                .paid(event1.getPaid())
                .title(event1.getTitle())
                .views(5L)
                .build();
        eventDtoOutShort2 = EventDtoOutShort.builder()
                .id(event2.getId())
                .annotation(event2.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event2.getRequests().stream().count())
                .eventDate(event2.getEventDate())
                .initiator(userDto2)
                .paid(event2.getPaid())
                .title(event2.getTitle())
                .views(10L)
                .build();
        request = Request.builder()
                .id(1L)
                .status(EventState.PUBLISHED)
                .requester(user1)
                .created(LocalDateTime.now())
                .eventId(event1)
                .build();
        requestDto = RequestDto.builder()
                .id(request.getId())
                .status(request.getStatus())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .eventId(request.getEventId().getId())
                .build();
        pageable = PageRequest.of(1, 10);
    }

    @Test
    void getEventsUser() {
        List<Event> events = new ArrayList<>();
        events.add(event1);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(eventRepository.findAllByInitiator(any(User.class), any(Pageable.class)))
                .thenReturn(events);
        when(commonService.addViewsToEventShort(any(Event.class)))
                .thenReturn(eventDtoOutShort1);
        List<EventDtoOutShort> testEvents = eventService.getEventsUser(user1.getId(), 0, 10)
                .stream().collect(Collectors.toList());
        assertThat(testEvents.get(0), equalTo(eventDtoOutShort1));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(eventRepository, times(1))
                .findAllByInitiator(any(User.class), any(Pageable.class));
        verify(commonService, times(1))
                .addViewsToEventShort(any(Event.class));
    }

    @Test
    void patchEvent() {
        EventDtoInPatch eventDtoInPatch = EventDtoInPatch.builder()
                .eventId(2L)
                .annotation("Patch annotation 10")
                .category(event2.getCategoryId().getId())
                .description(event2.getDescription())
                .eventDate(event2.getEventDate())
                .paid(event2.getPaid())
                .participantLimit(event2.getParticipantLimit())
                .title(event2.getTitle())
                .build();
        eventDtoOutFull2.setAnnotation(eventDtoInPatch.getAnnotation());
        eventDtoOutFull2.setState(EventState.CANCELED);
        event2.setState(EventState.CANCELED);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event2);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(event2.getInitiator());
        when(eventRepository.save(any(Event.class)))
                .thenReturn(event2);
        when(commonService.addViewsToEventFull(any(Event.class)))
                .thenReturn(eventDtoOutFull2);
        EventDtoOutFull testEvent = eventService.patchEvent(event2.getInitiator().getId(), eventDtoInPatch);
        assertThat(testEvent, equalTo(eventDtoOutFull2));
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(eventRepository, times(1))
                .save(any(Event.class));
        verify(commonService, times(1))
                .addViewsToEventFull(any(Event.class));
    }

    @Test
    void createEvent() {
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(eventRepository.save(any(Event.class)))
                .thenReturn(event1);
        when(commonService.addViewsToEventFull(any(Event.class)))
                .thenReturn(eventDtoOutFull1);
        EventDtoOutFull testEvent = eventService.createEvent(user1.getId(), eventDtoIn);
        assertThat(testEvent, equalTo(eventDtoOutFull1));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(eventRepository, times(1))
                .save(any(Event.class));
        verify(commonService, times(1))
                .addViewsToEventFull(any(Event.class));
    }

    @Test
    void getEventOfUser() {
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user2);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event2);
        when(commonService.addViewsToEventFull(any(Event.class)))
                .thenReturn(eventDtoOutFull2);
        EventDtoOutFull testEvent = eventService.getEventOfUser(user2.getId(), event2.getId());
        assertThat(testEvent, equalTo(eventDtoOutFull2));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(commonService, times(1))
                .addViewsToEventFull(any(Event.class));
    }

    @Test
    void cancelEventOfUser() {
        event1.setRequestModeration(true);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(eventRepository.save(any(Event.class)))
                .thenReturn(event1);
        when(commonService.addViewsToEventFull(any(Event.class)))
                .thenReturn(eventDtoOutFull1);
        EventDtoOutFull testEvent = eventService.cancelEventOfUser(user1.getId(), event1.getId());
        assertThat(testEvent, equalTo(eventDtoOutFull1));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(eventRepository, times(1))
                .save(any(Event.class));
        verify(commonService, times(1))
                .addViewsToEventFull(any(Event.class));
    }

    @Test
    void getRequestsInEventOfUser() {
        List<Request> requests = new ArrayList<>();
        requests.add(request);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(requestRepository.findAllByEventId(any(Event.class)))
                .thenReturn(requests);
        List<RequestDto> testRequest = eventService.getRequestsInEventOfUser(user1.getId(), event1.getId())
                .stream().collect(Collectors.toList());
        assertThat(testRequest, hasSize(1));
        assertThat(testRequest.get(0), equalTo(requestDto));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(requestRepository, times(1))
                .findAllByEventId(any(Event.class));
    }

    @Test
    void patchRequestInEventOfUserConfirm() {
        request.setStatus(EventState.PENDING);
        requestDto.setStatus(EventState.CONFIRMED);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(commonService.getRequestInDb(anyLong()))
                .thenReturn(request);
        when(requestRepository.save(any(Request.class)))
                .thenReturn(request);
        RequestDto testRequest = eventService.patchRequestInEventOfUserConfirm(user1.getId(), event1.getId(), request.getId());
        assertThat(testRequest, equalTo(requestDto));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(commonService, times(1))
                .getRequestInDb(anyLong());
        verify(requestRepository, times(1))
                .save(any(Request.class));
    }

    @Test
    void patchRequestInEventOfUserReject() {
        request.setStatus(EventState.PENDING);
        requestDto.setStatus(EventState.REJECTED);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(commonService.getRequestInDb(anyLong()))
                .thenReturn(request);
        when(requestRepository.save(any(Request.class)))
                .thenReturn(request);
        RequestDto testRequest = eventService.patchRequestInEventOfUserReject(user1.getId(), event1.getId(), request.getId());
        assertThat(testRequest, equalTo(requestDto));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(commonService, times(1))
                .getRequestInDb(anyLong());
        verify(requestRepository, times(1))
                .save(any(Request.class));
    }

    @Test
    void getPublicEvents() {
        event1.setState(EventState.PUBLISHED);
        event2.setState(EventState.PUBLISHED);
        List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(eventRepository.findAllByState(any(EventState.class), any(Pageable.class)))
                .thenReturn(events);
        when(commonService.addViewsToEventShort(any(Event.class)))
                .thenReturn(eventDtoOutShort1)
                .thenReturn(eventDtoOutShort2);
        List<EventDtoOutShort> testEvents = eventService.getPublicEvents(
                        null, null, null, null, null,
                        null, EventPublicSort.VIEWS, 0, 10)
                .stream().collect(Collectors.toList());
        assertThat(testEvents, hasSize(2));
        assertThat(testEvents.get(0), equalTo(eventDtoOutShort1));
        assertThat(testEvents.get(1), equalTo(eventDtoOutShort2));
        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(eventRepository, times(1))
                .findAllByState(any(EventState.class), any(Pageable.class));
        verify(commonService, times(2))
                .addViewsToEventShort(any(Event.class));
    }

    @Test
    void getPublicEvent() {
        event2.setState(EventState.PUBLISHED);
        eventDtoOutFull2.setState(EventState.PUBLISHED);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event2);
        when(commonService.addViewsToEventFull(any(Event.class)))
                .thenReturn(eventDtoOutFull2);
        EventDtoOutFull testEvent = eventService.getPublicEvent(event2.getId());
        assertThat(testEvent, equalTo(eventDtoOutFull2));
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(commonService, times(1))
                .addViewsToEventFull(any(Event.class));
    }
}
