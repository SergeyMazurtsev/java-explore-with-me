package ru.practicum.ewm.admin;

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
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.Location;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminEventControllerTest {
    private MockMvc mvc;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminEventController adminEventController;
    private ObjectMapper mapper = new ObjectMapper();
    private EventDtoIn eventDtoIn;
    private EventDtoOutFull eventDtoOutFull1;
    private EventDtoOutFull eventDtoOutFull2;
    private String url = "/admin/events";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(adminEventController).build();
        mapper.registerModule(new JavaTimeModule());
        eventDtoIn = EventDtoIn.builder()
                .id(1L)
                .annotation("Testing annotation")
                .category(1L)
                .description("Testing description")
                .eventDate(LocalDateTime.now())
                .location(new Location(430d, 270d))
                .paid(true)
                .participantLimit(10)
                .requestModeration(true)
                .title("Testing title")
                .build();
        eventDtoOutFull1 = EventDtoOutFull.builder()
                .id(1L)
                .annotation("Testing annotation")
                .category(CategoryDto.builder().id(1L).name("Category").build())
                .confirmedRequests(5L)
                .createdOn(LocalDateTime.now())
                .description("Testing description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(UserDto.builder().id(1L).name("Sasha").email("qweert@ww.ru").build())
                .location(new Location(430d, 270d))
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(2))
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Testing title")
                .views(2L)
                .build();
        eventDtoOutFull2 = EventDtoOutFull.builder()
                .id(2L)
                .annotation("Testing annotation 2")
                .category(CategoryDto.builder().id(1L).name("Category").build())
                .confirmedRequests(5L)
                .createdOn(LocalDateTime.now())
                .description("Testing description 2")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(UserDto.builder().id(1L).name("Sasha").email("qweert@ww.ru").build())
                .location(new Location(430d, 270d))
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(2))
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Testing title 2")
                .views(2L)
                .build();
    }

    @Test
    void getEvents() throws Exception {
        Collection<EventDtoOutFull> dtoOutFullCollection = new ArrayList<>();
        dtoOutFullCollection.add(eventDtoOutFull1);
        dtoOutFullCollection.add(eventDtoOutFull2);
        when(adminService.getEvents(anyList(), anyList(), anyList(), any(),
                any(), anyInt(), anyInt()))
                .thenReturn(dtoOutFullCollection);
        mvc.perform(get(url)
                        .param("users", "1,2")
                        .param("states", EventState.PUBLISHED.toString())
                        .param("categories", "1")
                        .param("rangeStart", "2022-10-21 04:33:09")
                        .param("rangeEnd", "2022-10-21 04:33:09")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(eventDtoOutFull1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(eventDtoOutFull2.getId()), Long.class));
        verify(adminService, times(1))
                .getEvents(anyList(), anyList(), anyList(), any(),
                        any(), anyInt(), anyInt());
    }

    @Test
    void patchEvent() throws Exception {
        eventDtoOutFull1.setAnnotation("Patched");
        eventDtoIn.setAnnotation("Patched");
        when(adminService.patchEvent(anyLong(), any(EventDtoIn.class)))
                .thenReturn(eventDtoOutFull1);
        mvc.perform(put(url + "/" + eventDtoOutFull1.getId())
                        .content(mapper.writeValueAsString(eventDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.annotation", is(eventDtoOutFull1.getAnnotation())));

        verify(adminService, times(1))
                .patchEvent(anyLong(), any(EventDtoIn.class));
    }

    @Test
    void publishEvent() throws Exception {
        eventDtoOutFull1.setState(EventState.PUBLISHED);
        when(adminService.publishEvent(anyLong()))
                .thenReturn(eventDtoOutFull1);
        mvc.perform(patch(url + "/" + eventDtoOutFull1.getId() + "/publish")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is(eventDtoOutFull1.getState().toString())));

        verify(adminService, times(1))
                .publishEvent(anyLong());
    }

    @Test
    void rejectEvent() throws Exception {
        eventDtoOutFull1.setState(EventState.CANCELED);
        when(adminService.rejectEvent(anyLong()))
                .thenReturn(eventDtoOutFull1);
        mvc.perform(patch(url + "/" + eventDtoOutFull1.getId() + "/reject")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state", is(eventDtoOutFull1.getState().toString())));

        verify(adminService, times(1))
                .rejectEvent(anyLong());
    }
}
