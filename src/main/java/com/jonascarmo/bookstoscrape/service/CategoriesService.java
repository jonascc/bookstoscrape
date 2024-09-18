package com.jonascarmo.bookstoscrape.service;

import com.jonascarmo.bookstoscrape.dao.CategoryDao;
import com.jonascarmo.bookstoscrape.dto.CategoryDto;
import com.jonascarmo.bookstoscrape.mapper.CategoryMapper;
import com.jonascarmo.bookstoscrape.model.Category;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriesService {

    private final CategoryDao categoryDao;

    private final CategoryMapper mapper = Mappers.getMapper(CategoryMapper.class);

    public CategoryDto findById(String id) {
        Category category = categoryDao.findById(id);
        return category != null ? mapper.categoryToCategoryDto(category) : null;
    }

    public List<CategoryDto> findAll() {
        List<Category> categories = categoryDao.findAll();
        return mapper.categoryListToCategoryDtoList(categories);
    }

    public CategoryDto save(CategoryDto categoryDto) {
        categoryDto.setId(UUID.randomUUID());
        Category savedCategory = categoryDao.save(mapper.categoryDtoToCategory(categoryDto));
        return mapper.categoryToCategoryDto(savedCategory);
    }

    public void update(CategoryDto updatedCategory) {
        categoryDao.update(mapper.categoryDtoToCategory(updatedCategory));
    }

    public void delete(String id) {
        categoryDao.delete(id);
    }

}
