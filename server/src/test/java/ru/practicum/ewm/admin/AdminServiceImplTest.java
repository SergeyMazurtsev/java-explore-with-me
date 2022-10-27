package ru.practicum.ewm.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.compilations.CompilationRepository;
import ru.practicum.ewm.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.EventDtoOutShort;
import ru.practicum.ewm.events.dto.Location;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.model.Request;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
    private final EventRepository eventRepository = Mockito.mock(EventRepository.class);
    private final CompilationRepository compilationRepository = Mockito.mock(CompilationRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);
    private final AdminService adminService = new AdminServiceImpl(
            userRepository, categoryRepository, eventRepository, compilationRepository, commonService);

    private Category category;
    private CategoryDto categoryDto;
    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private Event event1;
    private Event event2;
    private EventDtoIn eventDtoIn;
    private EventDtoOutFull eventDtoOutFull1;
    private EventDtoOutFull eventDtoOutFull2;
    private EventDtoOutShort eventDtoOutShort1;
    private EventDtoOutShort eventDtoOutShort2;
    private Compilation compilation;
    private CompilationDtoIn compilationDtoIn;
    private CompilationDtoOut compilationDtoOut;

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
                .requests(new HashSet<>(Arrays.asList(Request.builder().id(1L).status(EventState.PUBLISHED).build())))
                .build();
        event2 = Event.builder()
                .id(2L)
                .annotation("Testing event2")
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
                .requests(new HashSet<>(Arrays.asList(Request.builder().id(1L).status(EventState.PUBLISHED).build())))
                .build();
        eventDtoOutFull1 = EventDtoOutFull.builder()
                .id(event1.getId())
                .annotation(event1.getAnnotation())
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
                .confirmedRequests(0L)
                .eventDate(event1.getEventDate())
                .initiator(userDto1)
                .paid(event1.getPaid())
                .title(event1.getTitle())
                .build();
        eventDtoOutShort2 = EventDtoOutShort.builder()
                .id(event2.getId())
                .annotation(event2.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(0L)
                .eventDate(event2.getEventDate())
                .initiator(userDto1)
                .paid(event2.getPaid())
                .title(event2.getTitle())
                .build();
        compilation = Compilation.builder()
                .id(1L)
                .pinned(true)
                .title("Testing compilation")
                .events(Set.of(event1, event2))
                .build();
        compilationDtoIn = CompilationDtoIn.builder()
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(Set.of(event1.getId(), event2.getId()))
                .build();
        compilationDtoOut = CompilationDtoOut.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(Set.of(eventDtoOutShort1, eventDtoOutShort2))
                .build();
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user1);
        UserDto testUser1 = adminService.createUser(userDto1);
        assertThat(testUser1.getId(), equalTo(userDto1.getId()));
        assertThat(testUser1.getName(), equalTo(userDto1.getName()));
        assertThat(testUser1.getEmail(), equalTo(userDto1.getEmail()));

        verify(userRepository, times(1))
                .save(any(User.class));
    }

    @Test
    void getUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1))
                .thenReturn(Optional.ofNullable(user2));

        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(userDto1);
        userDtos.add(userDto2);
        List<UserDto> testUsers = adminService.getUsers(List.of(userDto1.getId(), userDto2.getId()), 0, 10)
                .stream().collect(Collectors.toList());
        assertThat(testUsers, hasSize(userDtos.size()));
        assertThat(testUsers.get(0), equalTo(userDtos.get(0)));
        assertThat(testUsers.get(1), equalTo(userDtos.get(1)));
        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(userRepository, times(2))
                .findById(anyLong());
    }

    @Test
    void deleteUser() {
        doNothing().when(userRepository).deleteById(anyLong());
        adminService.deleteUser(userDto1.getId());

        verify(userRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void createCategory() {
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);
        CategoryDto testCat = adminService.createCategory(categoryDto);
        assertThat(testCat.getId(), equalTo(categoryDto.getId()));
        assertThat(testCat.getName(), equalTo(categoryDto.getName()));

        verify(categoryRepository, times(1))
                .save(any(Category.class));
    }

    @Test
    void patchCategory() {
        categoryDto.setName("Patched Testing");
        when(commonService.getCategoryInDb(anyLong()))
                .thenReturn(category);
        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);
        CategoryDto testCat = adminService.patchCategory(categoryDto);
        assertThat(testCat.getName(), equalTo(categoryDto.getName()));

        verify(commonService, times(1))
                .getCategoryInDb(anyLong());
        verify(categoryRepository, times(1))
                .save(any(Category.class));
    }

    @Test
    void deleteCategory() {
        doNothing().when(categoryRepository).deleteById(anyLong());
        adminService.deleteCategory(category.getId());

        verify(categoryRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void getEvents() {
        List<Event> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);
        Pageable pageable = PageRequest.of(0, 10);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1)
                .thenReturn(user2);
        when(eventRepository.findAllByInitiator(any(User.class)))
                .thenReturn(List.of(event1))
                .thenReturn(List.of(event2));
        when(commonService.filterEventsByCategory(anyList(), any()))
                .thenReturn(events);
        when(commonService.filterEventsByRangeStart(anyList(), any()))
                .thenReturn(events);
        when(commonService.filterEventsByRangeEnd(anyList(), any()))
                .thenReturn(events);
        when(commonService.addViewsToEventFull(event1))
                .thenReturn(eventDtoOutFull1);
        when(commonService.addViewsToEventFull(event2))
                .thenReturn(eventDtoOutFull2);
        List<EventDtoOutFull> testEvents = adminService.getEvents(
                        List.of(1L, 2L), null, null, null, null, 0, 10)
                .stream().collect(Collectors.toList());
        assertThat(testEvents.size(), equalTo(2));
        assertThat(testEvents.get(0), equalTo(eventDtoOutFull1));
        assertThat(testEvents.get(1), equalTo(eventDtoOutFull2));

        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(commonService, times(2))
                .getUserInDb(anyLong());
        verify(eventRepository, times(2))
                .findAllByInitiator(any(User.class));
        verify(commonService, times(1))
                .filterEventsByCategory(anyList(), any());
        verify(commonService, times(1))
                .filterEventsByRangeStart(anyList(), any());
        verify(commonService, times(1))
                .filterEventsByRangeEnd(anyList(), any());
        verify(commonService, times(1))
                .addViewsToEventFull(event1);
        verify(commonService, times(1))
                .addViewsToEventFull(event2);
    }

    @Test
    void patchEvent() {
        eventDtoIn.setAnnotation("Patch testing event 1");
        eventDtoOutFull1.setAnnotation("Patch testing event 1");
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(eventRepository.save(any(Event.class)))
                .thenReturn(event1);
        when(commonService.addViewsToEventFull(event1))
                .thenReturn(eventDtoOutFull1);
        EventDtoOutFull testEvent = adminService.patchEvent(1L, eventDtoIn);
        assertThat(testEvent, equalTo(eventDtoOutFull1));

        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(eventRepository, times(1))
                .save(any(Event.class));
        verify(commonService, times(1))
                .addViewsToEventFull(event1);
    }

    @Test
    void publishEvent() {
        eventDtoOutFull1.setState(EventState.PUBLISHED);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(eventRepository.save(any(Event.class)))
                .thenReturn(event1);
        when(commonService.addViewsToEventFull(event1))
                .thenReturn(eventDtoOutFull1);
        EventDtoOutFull testEvent = adminService.publishEvent(1L);
        assertThat(testEvent, equalTo(eventDtoOutFull1));

        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(eventRepository, times(1))
                .save(any(Event.class));
        verify(commonService, times(1))
                .addViewsToEventFull(event1);
    }

    @Test
    void rejectEvent() {
        eventDtoOutFull1.setState(EventState.CANCELED);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(eventRepository.save(any(Event.class)))
                .thenReturn(event1);
        when(commonService.addViewsToEventFull(event1))
                .thenReturn(eventDtoOutFull1);
        EventDtoOutFull testEvent = adminService.rejectEvent(1L);
        assertThat(testEvent, equalTo(eventDtoOutFull1));

        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(eventRepository, times(1))
                .save(any(Event.class));
        verify(commonService, times(1))
                .addViewsToEventFull(event1);
    }

    @Test
    void createCompilation() {
        when(compilationRepository.save(any(Compilation.class)))
                .thenReturn(compilation);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1)
                .thenReturn(event2);
        CompilationDtoOut testCompilation = adminService.createCompilation(compilationDtoIn);
        assertThat(testCompilation, equalTo(compilationDtoOut));

        verify(compilationRepository, times(1))
                .save(any(Compilation.class));
        verify(commonService, times(2))
                .getEventInDb(anyLong());
    }

    @Test
    void deleteCompilation() {
        doNothing().when(compilationRepository).deleteById(anyLong());
        adminService.deleteCompilation(compilation.getId());

        verify(compilationRepository, times(1))
                .deleteById(anyLong());
    }

    @Test
    void deleteEventFromCompilation() {
        when(commonService.getCompilationInDb(anyLong()))
                .thenReturn(compilation);
        when(compilationRepository.save(any(Compilation.class)))
                .thenReturn(compilation);

        adminService.deleteEventFromCompilation(compilation.getId(), event2.getId());
        ArgumentCaptor<Compilation> captor = ArgumentCaptor.forClass(Compilation.class);
        verify(commonService, times(1))
                .getCompilationInDb(anyLong());
        verify(compilationRepository, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getEvents(), equalTo(compilation.getEvents()));
    }

    @Test
    void addEventToCompilation() {
        Set<Event> events = new HashSet<>();
        events.add(event1);
        compilation.setEvents(events);
        when(commonService.getCompilationInDb(anyLong()))
                .thenReturn(compilation);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event2);
        when(compilationRepository.save(any(Compilation.class)))
                .thenReturn(compilation);

        adminService.addEventToCompilation(compilation.getId(), event2.getId());
        ArgumentCaptor<Compilation> captor = ArgumentCaptor.forClass(Compilation.class);
        verify(commonService, times(1))
                .getCompilationInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(compilationRepository, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getEvents(), equalTo(compilation.getEvents()));
    }

    @Test
    void unpinCompilation() {
        when(commonService.getCompilationInDb(anyLong()))
                .thenReturn(compilation);
        when(compilationRepository.save(any(Compilation.class)))
                .thenReturn(compilation);

        adminService.unpinCompilation(compilation.getId());
        ArgumentCaptor<Compilation> captor = ArgumentCaptor.forClass(Compilation.class);
        verify(commonService, times(1))
                .getCompilationInDb(anyLong());
        verify(compilationRepository, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getPinned(), equalTo(compilation.getPinned()));
    }

    @Test
    void pinCompilation() {
        compilation.setPinned(false);
        when(commonService.getCompilationInDb(anyLong()))
                .thenReturn(compilation);
        when(compilationRepository.save(any(Compilation.class)))
                .thenReturn(compilation);

        adminService.pinCompilation(compilation.getId());
        ArgumentCaptor<Compilation> captor = ArgumentCaptor.forClass(Compilation.class);
        verify(commonService, times(1))
                .getCompilationInDb(anyLong());
        verify(compilationRepository, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getPinned(), equalTo(compilation.getPinned()));
    }
}
