package ru.practicum.ewm.requests;

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
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.requests.dto.RequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestPrivateControllerTest {
    @Mock
    private RequestService requestService;
    @InjectMocks
    private RequestPrivateController requestPrivateController;
    private MockMvc mvc;
    private ObjectMapper mapper = new ObjectMapper();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private RequestDto requestDto1;
    private RequestDto requestDto2;
    private String url = "/users";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(requestPrivateController).build();
        mapper.registerModule(new JavaTimeModule());
        requestDto1 = RequestDto.builder()
                .id(5L)
                .created(LocalDateTime.now().minusHours(1))
                .eventId(10L)
                .requester(2L)
                .status(EventState.PENDING)
                .build();
        requestDto2 = RequestDto.builder()
                .id(25L)
                .created(LocalDateTime.now().minusHours(1))
                .eventId(19L)
                .requester(2L)
                .status(EventState.PENDING)
                .build();
    }

    @Test
    void getRequestsOfUser() throws Exception {
        Collection<RequestDto> requestDtos = new ArrayList<>();
        requestDtos.add(requestDto1);
        requestDtos.add(requestDto2);
        when(requestService.getRequestsOfUser(anyLong()))
                .thenReturn(requestDtos);
        mvc.perform(get(url + "/" + requestDto1.getRequester() + "/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].created",
                        is(dateTimeFormatter.format(requestDto1.getCreated()))))
                .andExpect(jsonPath("$[1].id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].created",
                        is(dateTimeFormatter.format(requestDto2.getCreated()))));
        verify(requestService, times(1))
                .getRequestsOfUser(anyLong());
    }

    @Test
    void createRequestForUser() throws Exception {
        when(requestService.createRequestForUser(anyLong(), anyLong()))
                .thenReturn(requestDto1);
        mvc.perform(post(url + "/" + requestDto1.getRequester() + "/requests")
                        .param("eventId", requestDto1.getEventId().toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto1.getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(dateTimeFormatter.format(requestDto1.getCreated()))));
        verify(requestService, times(1))
                .createRequestForUser(anyLong(), anyLong());
    }

    @Test
    void cancelRequestByUser() throws Exception {
        when(requestService.cancelRequestByUser(anyLong(), anyLong()))
                .thenReturn(requestDto2);
        mvc.perform(patch(url + "/" + requestDto2.getRequester() + "/requests/"
                        + requestDto2.getId() + "/cancel")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto2.getId()), Long.class))
                .andExpect(jsonPath("$.created",
                        is(dateTimeFormatter.format(requestDto2.getCreated()))));
        verify(requestService, times(1))
                .cancelRequestByUser(anyLong(), anyLong());
    }
}
