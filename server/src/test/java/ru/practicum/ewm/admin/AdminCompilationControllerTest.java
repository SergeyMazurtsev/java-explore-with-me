package ru.practicum.ewm.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.events.dto.EventDtoOutShort;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminCompilationControllerTest {
    private MockMvc mvc;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminCompilationController adminCompilationController;
    private ObjectMapper mapper = new ObjectMapper();
    private CompilationDtoIn compilationDtoIn;
    private CompilationDtoOut compilationDtoOut;
    private final String url = "/admin/compilations";
    private Set<Long> events = new HashSet<>();

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(adminCompilationController).build();
        Set<Long> eventsId = new HashSet<>();
        events.add(1L);
        events.add(2L);
        Set<EventDtoOutShort> events = new HashSet<>();
        events.add(EventDtoOutShort.builder().id(1L).build());
        events.add(EventDtoOutShort.builder().id(2L).build());
        compilationDtoIn = CompilationDtoIn.builder()
                .events(eventsId)
                .pinned(true)
                .title("Testing")
                .build();
        compilationDtoOut = CompilationDtoOut.builder()
                .id(1L)
                .events(events)
                .pinned(true)
                .title("Testing")
                .build();
    }

    @Test
    void createCompilation() throws Exception {
        when(adminService.createCompilation(any(CompilationDtoIn.class)))
                .thenReturn(compilationDtoOut);
        mvc.perform(post(url)
                        .content(mapper.writeValueAsString(compilationDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(compilationDtoOut.getId()), Long.class))
                .andExpect(jsonPath("$.pinned", is(compilationDtoOut.getPinned())))
                .andExpect(jsonPath("$.title", is(compilationDtoOut.getTitle())))
                .andExpect(jsonPath("$.events", hasSize(2)));

        verify(adminService, times(1))
                .createCompilation(any(CompilationDtoIn.class));
    }

    @Test
    void deleteCompilation() throws Exception {
        doNothing().when(adminService).deleteCompilation(anyLong());
        mvc.perform(delete(url + "/" + compilationDtoOut.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService, times(1))
                .deleteCompilation(anyLong());
    }

    @Test
    void deleteEventFromCompilation() throws Exception {
        doNothing().when(adminService).deleteEventFromCompilation(anyLong(), anyLong());
        mvc.perform(delete(url + "/" + compilationDtoOut.getId() + "/events/" + 2L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService, times(1))
                .deleteEventFromCompilation(anyLong(), anyLong());
    }

    @Test
    void addEventToCompilation() throws Exception {
        doNothing().when(adminService).addEventToCompilation(anyLong(), anyLong());
        mvc.perform(patch(url + "/" + compilationDtoOut.getId() + "/events/" + 3L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService, times(1))
                .addEventToCompilation(anyLong(), anyLong());
    }

    @Test
    void unpinCompilation() throws Exception {
        doNothing().when(adminService).unpinCompilation(anyLong());
        mvc.perform(delete(url + "/" + compilationDtoOut.getId() + "/pin")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService, times(1))
                .unpinCompilation(anyLong());
    }

    @Test
    void pinCompilation() throws Exception {
        doNothing().when(adminService).pinCompilation(anyLong());
        mvc.perform(patch(url + "/" + compilationDtoOut.getId() + "/pin")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(adminService, times(1))
                .pinCompilation(anyLong());
    }
}
