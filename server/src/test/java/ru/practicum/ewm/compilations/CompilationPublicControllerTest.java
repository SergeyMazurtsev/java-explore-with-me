package ru.practicum.ewm.compilations;

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
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CompilationPublicControllerTest {
    private MockMvc mvc;
    @Mock
    private CompilationService compilationService;
    @InjectMocks
    private CompilationPublicController compilationPublicController;
    private ObjectMapper mapper = new ObjectMapper();
    private CompilationDtoOut compilationDtoOut1;
    private CompilationDtoOut compilationDtoOut2;
    private final String url = "/compilations";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(compilationPublicController).build();
        compilationDtoOut1 = CompilationDtoOut.builder()
                .id(1L)
                .pinned(true)
                .title("Testing title 1")
                .events(new HashSet<>(Arrays.asList(2L)))
                .build();
        compilationDtoOut2 = CompilationDtoOut.builder()
                .id(2L)
                .pinned(false)
                .title("Testing title 2")
                .events(new HashSet<>(Arrays.asList(1L)))
                .build();
    }

    @Test
    void getCompilations() throws Exception {
        Collection<CompilationDtoOut> compilationDtoOuts = new ArrayList<>();
        compilationDtoOuts.add(compilationDtoOut1);
        compilationDtoOuts.add(compilationDtoOut2);
        when(compilationService.getCompilations(anyBoolean(), anyInt(), anyInt()))
                .thenReturn(compilationDtoOuts);
        mvc.perform(get(url)
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(compilationDtoOut1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(compilationDtoOut2.getId()), Long.class));

        verify(compilationService, times(1))
                .getCompilations(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getCompilation() throws Exception {
        when(compilationService.getCompilation(anyLong()))
                .thenReturn(compilationDtoOut2);
        mvc.perform(get(url + "/" + compilationDtoOut2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(compilationDtoOut2.getId()), Long.class));

        verify(compilationService, times(1))
                .getCompilation(anyLong());
    }
}
