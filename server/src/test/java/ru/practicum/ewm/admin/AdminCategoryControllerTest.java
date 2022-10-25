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
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.model.Category;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminCategoryControllerTest {
    private MockMvc mvc;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminCategoryController adminCategoryController;
    private ObjectMapper mapper = new ObjectMapper();

    private Category category;
    private CategoryDto categoryDto;
    private final String url = "/admin/categories";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(adminCategoryController).build();
        mapper = new ObjectMapper();
        category = Category.builder()
                .id(1L)
                .name("Testing")
                .build();
        categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Test
    void createCategory() throws Exception {
        when(adminService.createCategory(any(CategoryDto.class)))
                .thenReturn(categoryDto);
        mvc.perform(post(url)
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));

        verify(adminService, times(1))
                .createCategory(any(CategoryDto.class));
    }

    @Test
    void patchCategory() throws Exception {
        categoryDto.setName("Testing2");
        when(adminService.patchCategory(any(CategoryDto.class)))
                .thenReturn(categoryDto);
        mvc.perform(patch(url)
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));

        verify(adminService, times(1))
                .patchCategory(any(CategoryDto.class));
    }

    @Test
    void deleteCategory() throws Exception {
        doNothing().when(adminService).deleteCategory(anyLong());
        mvc.perform(delete(url + "/{catId}", categoryDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(adminService, times(1))
                .deleteCategory(anyLong());
    }
}
