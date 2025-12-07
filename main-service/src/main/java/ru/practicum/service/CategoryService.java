package ru.practicum.service;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long id);

    CategoryDto patchCategory(Long id, NewCategoryDto categoryDto);

    List<CategoryDto> findCategories(int from, int size);

    CategoryDto getCategoryById(long id);
}
