package com.jonascarmo.bookstoscrape.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class CategoryDto {

    private UUID id;
    private String categoryName;
    private String categoryUrl;

}
