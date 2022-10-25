package ru.practicum.ewm.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.requests.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventPrivateControllerTest {
    private MockMvc mvc;
    @Mock
    private EventService eventService;
    @InjectMocks
    private EventPrivateController eventPrivateController;
    private ObjectMapper mapper = new ObjectMapper();
    private EventDtoOutFull eventDtoOutFull;
    private EventDtoOutShort eventDtoOutShort;
    private EventDtoInPatch eventDtoInPatch;
    private EventDtoIn eventDtoIn;
    private RequestDto requestDto;
    private final String url = "/users";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(eventPrivateController).build();
        mapper.registerModule(new JavaTimeModule());
        eventDtoOutFull = EventDtoOutFull.builder()
                .id(1L)
                .annotation("Test annotation")
                .category(CategoryDto.builder().id(1L).build())
                .confirmedRequests(2L)
                .createdOn(LocalDateTime.now())
                .description("Test description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(UserDto.builder().id(1L).build())
                .location(new Location(333d, 444d))
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Test title")
                .views(4L)
                .build();
        eventDtoOutShort = EventDtoOutShort.builder()
                .id(eventDtoOutFull.getId())
                .annotation(eventDtoOutFull.getAnnotation())
                .category(eventDtoOutFull.getCategory())
                .confirmedRequests(eventDtoOutFull.getConfirmedRequests())
                .eventDate(eventDtoOutFull.getEventDate())
                .initiator(eventDtoOutFull.getInitiator())
                .paid(eventDtoOutFull.getPaid())
                .title(eventDtoOutFull.getTitle())
                .views(eventDtoOutFull.getViews())
                .build();
        eventDtoInPatch = EventDtoInPatch.builder()
                .eventId(1L)
                .annotation(eventDtoOutFull.getAnnotation())
                .category(eventDtoOutFull.getCategory().getId())
                .description(eventDtoOutFull.getDescription())
                .eventDate(eventDtoOutFull.getEventDate())
                .paid(eventDtoOutFull.getPaid())
                .participantLimit(eventDtoOutFull.getParticipantLimit())
                .title(eventDtoOutFull.getTitle())
                .build();
        eventDtoIn = EventDtoIn.builder()
                .id(eventDtoOutFull.getId())
                .annotation(eventDtoOutFull.getAnnotation())
                .category(eventDtoOutFull.getCategory().getId())
                .description(eventDtoOutFull.getDescription())
                .eventDate(eventDtoOutFull.getEventDate())
                .location(eventDtoOutFull.getLocation())
                .paid(eventDtoOutFull.getPaid())
                .participantLimit(eventDtoOutFull.getParticipantLimit())
                .requestModeration(eventDtoOutFull.getRequestModeration())
                .title(eventDtoOutFull.getTitle())
                .build();
        requestDto = RequestDto.builder()
                .id(4L)
                .created(LocalDateTime.now())
                .eventId(eventDtoOutFull.getId())
                .requester(eventDtoOutFull.getInitiator().getId())
                .status(EventState.PUBLISHED)
                .build();
    }

    @Test
    void getEventsUser() throws Exception {
        Collection<EventDtoOutShort> events = new ArrayList<>();
        events.add(eventDtoOutShort);
        when(eventService.getEventsUser(anyLong(), anyInt(), anyInt()))
                .thenReturn(events);
        mvc.perform(get(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(eventDtoOutShort.getId()), Long.class));
        verify(eventService, times(1))
                .getEventsUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void patchEvent() throws Exception {
        eventDtoInPatch.setAnnotation("Patch annotation");
        eventDtoOutFull.setAnnotation("Patch annotation");
        when(eventService.patchEvent(anyLong(), any(EventDtoInPatch.class)))
                .thenReturn(eventDtoOutFull);
        mvc.perform(patch(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events")
                        .content(mapper.writeValueAsString(eventDtoInPatch))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventDtoOutFull.getId()), Long.class));
        verify(eventService, times(1))
                .patchEvent(anyLong(), any(EventDtoInPatch.class));
    }

    @Test
    void createEvent() throws Exception {
        when(eventService.createEvent(anyLong(), any(EventDtoIn.class)))
                .thenReturn(eventDtoOutFull);
        mvc.perform(post(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events")
                        .content(mapper.writeValueAsString(eventDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventDtoOutFull.getId()), Long.class));
        verify(eventService, times(1))
                .createEvent(anyLong(), any(EventDtoIn.class));
    }

    @Test
    void getEventOfUser() throws Exception {
        when(eventService.getEventOfUser(anyLong(), anyLong()))
                .thenReturn(eventDtoOutFull);
        mvc.perform(get(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events/"
                        + eventDtoOutFull.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventDtoOutFull.getId()), Long.class));
        verify(eventService, times(1))
                .getEventOfUser(anyLong(), anyLong());
    }

    @Test
    void cancelEventOfUser() throws Exception {
        eventDtoOutFull.setState(EventState.CANCELED);
        when(eventService.cancelEventOfUser(anyLong(), anyLong()))
                .thenReturn(eventDtoOutFull);
        mvc.perform(patch(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events/"
                        + eventDtoOutFull.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is(eventDtoOutFull.getState().toString())));
        verify(eventService, times(1))
                .cancelEventOfUser(anyLong(), anyLong());
    }

    @Test
    void getRequestsInEventOfUser() throws Exception {
        Collection<RequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(requestDto);
        when(eventService.getRequestsInEventOfUser(anyLong(), anyLong()))
                .thenReturn(requestDtos);
        mvc.perform(get(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events/"
                        + eventDtoOutFull.getId() + "/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].created",
                        is(dateTimeFormatter.format(requestDto.getCreated()))));

        verify(eventService, times(1))
                .getRequestsInEventOfUser(anyLong(), anyLong());
    }

    @Test
    void patchRequestInEventOfUserConfirm() throws Exception {
        when(eventService.patchRequestInEventOfUserConfirm(anyLong(), anyLong(), anyLong()))
                .thenReturn(requestDto);
        mvc.perform(patch(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events/"
                        + eventDtoOutFull.getId() + "/requests/" + requestDto.getId() + "/confirm")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(dateTimeFormatter.format(requestDto.getCreated()))));

        verify(eventService, times(1))
                .patchRequestInEventOfUserConfirm(anyLong(), anyLong(), anyLong());
    }

    @Test
    void patchRequestInEventOfUserReject() throws Exception {
        when(eventService.patchRequestInEventOfUserReject(anyLong(), anyLong(), anyLong()))
                .thenReturn(requestDto);
        mvc.perform(patch(url + "/" + eventDtoOutShort.getInitiator().getId() + "/events/"
                        + eventDtoOutFull.getId() + "/requests/" + requestDto.getId() + "/reject")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(dateTimeFormatter.format(requestDto.getCreated()))));

        verify(eventService, times(1))
                .patchRequestInEventOfUserReject(anyLong(), anyLong(), anyLong());
    }
}
