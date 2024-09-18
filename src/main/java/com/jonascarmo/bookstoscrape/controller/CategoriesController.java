package com.jonascarmo.bookstoscrape.controller;

import com.jonascarmo.bookstoscrape.dto.CategoryDto;
import com.jonascarmo.bookstoscrape.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping("/{id}")
    private ResponseEntity<CategoryDto> findById(@PathVariable String id) {
        CategoryDto category = findCategory(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @GetMapping(value={"", "/"})
    private ResponseEntity<List<CategoryDto>> findAll() {
        List<CategoryDto> categories = categoriesService.findAll();
        return ResponseEntity.ok(categories);
    }

    @PostMapping(value={"", "/"})
    private ResponseEntity<Void> createCategory(@RequestBody CategoryDto categoryDto, UriComponentsBuilder ucb) {
        CategoryDto savedCategory = categoriesService.save(categoryDto);
        URI locationOfNewCategory = ucb
                .path("categories/{id}")
                .buildAndExpand(savedCategory.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewCategory).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCategory(@PathVariable String requestedId, @RequestBody CategoryDto categoryUpdate) {
        CategoryDto category = findCategory(requestedId);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        CategoryDto updatedCategory = new CategoryDto(category.getId(), categoryUpdate.getCategoryName(), categoryUpdate.getCategoryUrl());
        categoriesService.update(updatedCategory);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        CategoryDto category = findCategory(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        categoriesService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryDto findCategory(String id) {
        return categoriesService.findById(id);
    }

}
