package ru.practicum.ewm.categories;

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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryPublicControllerTest {
    private MockMvc mvc;
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private CategoryPublicController categoryPublicController;
    private ObjectMapper mapper = new ObjectMapper();
    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;
    private String url = "/categories";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(categoryPublicController).build();
        mapper.registerModule(new JavaTimeModule());
        categoryDto1 = CategoryDto.builder()
                .id(1L)
                .name("Testing Category 1")
                .build();
        categoryDto2 = CategoryDto.builder()
                .id(2L)
                .name("Testing Category 2")
                .build();
    }

    @Test
    void getCategories() throws Exception {
        Collection<CategoryDto> categoryDtos = new ArrayList<>();
        categoryDtos.add(categoryDto1);
        categoryDtos.add(categoryDto2);
        when(categoryService.getCategories(anyInt(), anyInt()))
                .thenReturn(categoryDtos);
        mvc.perform(get(url)
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(categoryDto1.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(categoryDto2.getId()), Long.class));
        verify(categoryService, times(1))
                .getCategories(anyInt(), anyInt());
    }

    @Test
    void getCategory() throws Exception {
        when(categoryService.getCategory(anyLong()))
                .thenReturn(categoryDto1);
        mvc.perform(get(url + "/" + categoryDto1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto1.getId()), Long.class));
        verify(categoryService, times(1))
                .getCategory(anyLong());
    }
}
