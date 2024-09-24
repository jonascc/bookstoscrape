package com.jonascarmo.bookstoscrape.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class BookDto {

    private UUID id;
    private String name;
    private String url;
    private String imgLink;
//    private Category category;
    private String availability;
    private String description;
    private String upc;
    private String productType;
    private String priceExcludingTax;
    private String priceIncludingTax;
    private String tax;
    private int numberOfReviews;
    private String currency;
    private int starRating;

}
