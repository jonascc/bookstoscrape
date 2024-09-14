package com.jonascarmo.bookstoscrape.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Book {

    private UUID id;
    private String name;
    private String url;
    private String imgLink;
    private Category category;

}
