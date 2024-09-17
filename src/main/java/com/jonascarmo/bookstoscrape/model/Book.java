package com.jonascarmo.bookstoscrape.model;

import com.jonascarmo.bookstoscrape.StarRating;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class Book {

    private UUID id;
    private String name;
    private String url;
    private String imgLink;
    private Category category;
    private String availability;
    private String description;
    private String upc;
    private String productType;
    private BigDecimal priceExcludingTax;
    private BigDecimal priceIncludingTax;
    private BigDecimal tax;
    private int numberOfReviews;
    private String currency;
    private StarRating starRating;

}
