package ru.practicum.ewm.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.EventDtoOutShort;
import ru.practicum.ewm.events.dto.Location;
import ru.practicum.ewm.statistics.StatisticClient;
import ru.practicum.ewm.statistics.model.EndpointHit;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EventPublicControllerTest {
    private MockMvc mvc;
    @Mock
    private EventService eventService;
    @Mock
    private StatisticClient statisticClient;
    @InjectMocks
    private EventPublicController eventPublicController;
    private ObjectMapper mapper = new ObjectMapper();
    private EventDtoOutFull eventDtoOutFull;
    private EventDtoOutShort eventDtoOutShort;
    private String url = "/events";
    private final String app = "EWM";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(eventPublicController).build();
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
    }

    @Test
    void getPublicEvents() throws Exception {
        Collection<EventDtoOutShort> collection = new ArrayList<>();
        collection.add(eventDtoOutShort);
        doNothing().when(statisticClient).postViewOfEvent(any(EndpointHit.class));
        when(eventService.getPublicEvents(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(collection);
        mvc.perform(get(url)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(eventDtoOutShort.getId()), Long.class))
                .andExpect(jsonPath("$[0].eventDate",
                        is(dateTimeFormatter.format(eventDtoOutShort.getEventDate()))))
                .andExpect(jsonPath("$[0].views", is(eventDtoOutShort.getViews()), Long.class));
        verify(eventService, times(1))
                .getPublicEvents(any(), any(), any(), any(), any(), any(), any(), anyInt(), anyInt());
        ArgumentCaptor<EndpointHit> captor = ArgumentCaptor.forClass(EndpointHit.class);
        verify(statisticClient, times(1))
                .postViewOfEvent(captor.capture());
        assertThat(captor.getValue().getApp(), equalTo(app));
    }

    @Test
    void getPublicEvent() throws Exception {
        doNothing().when(statisticClient).postViewOfEvent(any(EndpointHit.class));
        when(eventService.getPublicEvent(anyLong()))
                .thenReturn(eventDtoOutFull);
        mvc.perform(get(url + "/" + eventDtoOutFull.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(eventDtoOutShort.getId()), Long.class))
                .andExpect(jsonPath("$.eventDate",
                        is(dateTimeFormatter.format(eventDtoOutShort.getEventDate()))))
                .andExpect(jsonPath("$.views", is(eventDtoOutShort.getViews()), Long.class));
        verify(eventService, times(1))
                .getPublicEvent(anyLong());
        ArgumentCaptor<EndpointHit> captor = ArgumentCaptor.forClass(EndpointHit.class);
        verify(statisticClient, times(1))
                .postViewOfEvent(captor.capture());
        assertThat(captor.getValue().getApp(), equalTo(app));
    }
}
