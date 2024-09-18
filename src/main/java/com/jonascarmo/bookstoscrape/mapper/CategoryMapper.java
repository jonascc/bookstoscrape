package com.jonascarmo.bookstoscrape.mapper;

import com.jonascarmo.bookstoscrape.dto.CategoryDto;
import com.jonascarmo.bookstoscrape.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(source = "name", target = "categoryName")
    @Mapping(source = "url", target = "categoryUrl")
    CategoryDto categoryToCategoryDto(Category category);

    @Mapping(source = "categoryName", target = "name")
    @Mapping(source = "categoryUrl", target = "url")
    Category categoryDtoToCategory(CategoryDto categoryDto);

    @Mapping(source = "name", target = "categoryName")
    @Mapping(source = "url", target = "categoryUrl")
    List<CategoryDto> categoryListToCategoryDtoList(List<Category> categories);

}
