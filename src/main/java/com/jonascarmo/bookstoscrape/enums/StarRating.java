package com.jonascarmo.bookstoscrape.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum StarRating {

    ONE(1, "One"),
    TWO(2, "Two"),
    THREE(3, "Three"),
    FOUR(4, "Four"),
    FIVE(5, "Five");

    private final int numberOfStars;
    private final String value;

    StarRating(int numberOfStars, String value) {
        this.numberOfStars = numberOfStars;
        this.value = value;
    }

    public static StarRating getFromClassAttribute(String classAttribute) {
        String starValue = classAttribute.substring(classAttribute.lastIndexOf(" ") + 1);// the class attribute is in the form of "star-rating One"
        Optional<StarRating> starRatingEnum = Arrays.stream(StarRating.values())
                .filter(s -> s.value.equals(starValue))
                .findFirst();
        return starRatingEnum.orElse(null);
    }

}
