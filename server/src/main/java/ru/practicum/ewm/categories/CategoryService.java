package ru.practicum.ewm.categories;

import ru.practicum.ewm.admin.dto.CategoryDto;

import java.util.Collection;

public interface CategoryService {
    Collection<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategory(Long catId);
}
