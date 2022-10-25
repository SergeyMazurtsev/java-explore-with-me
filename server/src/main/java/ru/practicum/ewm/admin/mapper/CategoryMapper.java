package ru.practicum.ewm.admin.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.model.Category;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryDto toCategoryDto(Category category);
    Category toCategory(CategoryDto categoryDto);
    void patchingCategory(CategoryDto categoryDto, @MappingTarget Category category);
}
