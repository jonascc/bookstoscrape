package com.jonascarmo.bookstoscrape.model;

import java.util.UUID;

public record Category(UUID id, String name, String url) {

    @Override
    public String toString() {
        return name;
    }

}
