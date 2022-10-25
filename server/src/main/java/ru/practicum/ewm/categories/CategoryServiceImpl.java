package ru.practicum.ewm.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.mapper.CategoryMapper;
import ru.practicum.ewm.common.CommonService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CommonService commonService;

    @Override
    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(commonService.getPagination(from, size, null))
                .stream()
                .map(CategoryMapper.INSTANCE::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(Long catId) {
        return CategoryMapper.INSTANCE.toCategoryDto(commonService.getCategoryInDb(catId));
    }
}
