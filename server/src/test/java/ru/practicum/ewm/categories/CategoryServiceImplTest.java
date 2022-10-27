package ru.practicum.ewm.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.common.CommonService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    private final CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);
    private final CategoryService categoryService = new CategoryServiceImpl(categoryRepository, commonService);
    private Category category1;
    private Category category2;
    private CategoryDto categoryDto1;
    private CategoryDto categoryDto2;

    @BeforeEach
    void setUp() {
        category1 = Category.builder()
                .id(1L)
                .name("Testing category 1")
                .build();
        category2 = Category.builder()
                .id(2L)
                .name("Testing category 2")
                .build();
        categoryDto1 = CategoryDto.builder()
                .id(category1.getId())
                .name(category1.getName())
                .build();
        categoryDto2 = CategoryDto.builder()
                .id(category2.getId())
                .name(category2.getName())
                .build();
    }

    @Test
    void getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);
        Pageable pageable = PageRequest.of(0, 10);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(categories));
        List<CategoryDto> testCat = categoryService.getCategories(0, 10)
                .stream().collect(Collectors.toList());
        assertThat(testCat.size(), equalTo(2));
        assertThat(testCat.get(0), equalTo(categoryDto1));
        assertThat(testCat.get(1), equalTo(categoryDto2));

        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(categoryRepository, times(1))
                .findAll(any(Pageable.class));
    }

    @Test
    void getCategory() {
        when(commonService.getCategoryInDb(anyLong()))
                .thenReturn(category1);
        CategoryDto testCat = categoryService.getCategory(category1.getId());
        assertThat(testCat, equalTo(categoryDto1));

        verify(commonService, times(1))
                .getCategoryInDb(anyLong());
    }
}
