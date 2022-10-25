package ru.practicum.ewm.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.admin.UserRepository;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.compilations.CompilationRepository;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.EventDtoOutShort;
import ru.practicum.ewm.events.dto.Location;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.RequestRepository;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.statistics.StatisticClient;
import ru.practicum.ewm.statistics.model.ViewStats;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommonServiceTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
    private final EventRepository eventRepository = Mockito.mock(EventRepository.class);
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    private final CompilationRepository compilationRepository = Mockito.mock(CompilationRepository.class);
    private final StatisticClient statisticClient = Mockito.mock(StatisticClient.class);
    private final CommonService commonService = new CommonService(
            userRepository, categoryRepository, eventRepository, requestRepository, compilationRepository, statisticClient);

    private Long catId;
    private Long userId;
    private Long eventId;
    private Long reqId;
    private Long compId;
    private int from;
    private int size;
    private String sortField;
    private Category category;
    private org.springframework.data.domain.Pageable pageableSort;
    private Pageable pageable;
    private User user;
    private Event event;
    private EventDtoOutFull eventDtoOutFull;
    private EventDtoOutShort eventDtoOutShort;
    private Compilation compilation;
    private Request request;

    @BeforeEach
    void setUp() {
        catId = 1L;
        userId = 1L;
        eventId = 1L;
        reqId = 1L;
        compId = 1L;
        from = 0;
        size = 10;
        sortField = "id";
        category = Category.builder()
                .id(catId)
                .name("Testing")
                .build();
        user = User.builder()
                .id(userId)
                .name("Test1")
                .email("qwerty@qqq.ru")
                .build();
        pageableSort = PageRequest.of(from, size, Sort.by(sortField).descending());
        pageable = PageRequest.of(from, size);
        event = Event.builder()
                .id(eventId)
                .annotation("Testing event1")
                .categoryId(category)
                .createdOn(LocalDateTime.now())
                .description("Testing description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(user)
                .locationLon(10.10)
                .locationLat(12.1)
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Testing title.")
                .requests(new HashSet<>(Arrays.asList(Request.builder().id(1L).status(EventState.PUBLISHED).build())))
                .build();
        request = Request.builder()
                .id(reqId)
                .status(EventState.PUBLISHED)
                .requester(user)
                .created(LocalDateTime.now())
                .eventId(event)
                .build();
        compilation = Compilation.builder()
                .id(compId)
                .pinned(true)
                .title("Testing compilation")
                .events(new HashSet<>(Arrays.asList(event)))
                .build();
        eventDtoOutFull = EventDtoOutFull.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .confirmedRequests(0L)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .location(new Location(event.getLocationLat(), event.getLocationLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(2L)
                .build();
        eventDtoOutShort = EventDtoOutShort.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryDto.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build())
                .confirmedRequests(0L)
                .eventDate(event.getEventDate())
                .initiator(UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .build())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(3L)
                .build();
    }

    @Test
    void getUserInDb() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        User testUser = commonService.getUserInDb(userId);
        assertThat(testUser.getId(), equalTo(user.getId()));
        assertThat(testUser.getName(), equalTo(user.getName()));
        assertThat(testUser.getEmail(), equalTo(user.getEmail()));

        verify(userRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getCategoryInDb() {
        when(categoryRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(category));
        Category testCat = commonService.getCategoryInDb(catId);
        assertThat(testCat.getId(), equalTo(category.getId()));
        assertThat(testCat.getName(), equalTo(category.getName()));

        verify(categoryRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getPagination() {
        Pageable testPage = commonService.getPagination(from, size, null);
        assertThat(testPage.getPageNumber(), equalTo(pageable.getPageNumber()));
        assertThat(testPage.getPageSize(), equalTo(pageable.getPageSize()));

        Pageable testPageSort = commonService.getPagination(from, size, sortField);
        assertThat(testPageSort.getPageNumber(), equalTo(pageableSort.getPageNumber()));
        assertThat(testPageSort.getPageSize(), equalTo(pageableSort.getPageSize()));
        assertThat(testPageSort.getSort(), equalTo(pageableSort.getSort()));
    }

    @Test
    void getEventInDb() {
        when(eventRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(event));
        Event testEvent = commonService.getEventInDb(eventId);
        assertThat(testEvent, equalTo(event));

        verify(eventRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void addViewsToEventFull() {
        List<ViewStats> viewStats = new ArrayList<>();
        viewStats.add(ViewStats.builder()
                .app("Qwerty")
                .hits(2L)
                .uri("Qwerty")
                .build());
        viewStats.add(ViewStats.builder()
                .app("Qwerty")
                .hits(12L)
                .uri("Qwerty")
                .build());
        eventDtoOutFull.setViews(14L);
        when(statisticClient.getViewOfEvent(any(), any(), any(), any()))
                .thenReturn(viewStats);
        EventDtoOutFull testEvent = commonService.addViewsToEventFull(event);
        assertThat(testEvent, equalTo(eventDtoOutFull));

        verify(statisticClient, times(1))
                .getViewOfEvent(any(), any(), any(), any());
    }

    @Test
    void addViewsToEventShort() {
        List<ViewStats> viewStats = new ArrayList<>();
        viewStats.add(ViewStats.builder()
                .app("Qwerty")
                .hits(2L)
                .uri("Qwerty")
                .build());
        viewStats.add(ViewStats.builder()
                .app("Asdfgh")
                .hits(1L)
                .uri("Asdfgh")
                .build());
        when(statisticClient.getViewOfEvent(any(), any(), any(), any()))
                .thenReturn(viewStats);
        EventDtoOutShort testEvent = commonService.addViewsToEventShort(event);
        assertThat(testEvent, equalTo(eventDtoOutShort));

        verify(statisticClient, times(1))
                .getViewOfEvent(any(), any(), any(), any());
    }

    @Test
    void getRequestInDb() {
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(request));
        Request testRequest = commonService.getRequestInDb(reqId);
        assertThat(testRequest, equalTo(request));

        verify(requestRepository, times(1))
                .findById(anyLong());
    }

    @Test
    void getCompilationInDb() {
        when(compilationRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(compilation));
        Compilation testComp = commonService.getCompilationInDb(compId);
        assertThat(testComp, equalTo(compilation));

        verify(compilationRepository, times(1))
                .findById(anyLong());
    }
}
