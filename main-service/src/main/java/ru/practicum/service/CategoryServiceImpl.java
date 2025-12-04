package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto saveCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.findByName(newCategoryDto.getName()).isPresent()) {
            throw new AlreadyExistsException("Категория с называнием  " + newCategoryDto.getName() + " существует");
        }
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(CategoryMapper.mapToCategory(newCategoryDto)));
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(Long id, CategoryDto categoryDto) {
        Category categoryForPatch = categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Категории с id=" + id + " не существует"));
        if (StringUtils.hasText(categoryDto.getName())) {
            if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
                throw new AlreadyExistsException("Категория с называнием  " + categoryDto.getName() + " существует");
            }
            categoryForPatch.setName(categoryDto.getName());
        }
        return CategoryMapper.mapToCategoryDto(categoryRepository.save(categoryForPatch));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("События с id=" + id + " не существует");
        }
        //Добавить проверку связи категории с событиями
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> findCategories(int from, int size) {
        List<Category> categories;
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Category> page = categoryRepository.findAll(pageable);
        categories = page.getContent();
        return CategoryMapper.mapToDtoList(categories);
    }

    @Override
    public CategoryDto getCategoryById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Категории с id=" + id + " не найдено"));
        return CategoryMapper.mapToCategoryDto(category);
    }
}
