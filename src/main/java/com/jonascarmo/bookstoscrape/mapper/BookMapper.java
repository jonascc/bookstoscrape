package com.jonascarmo.bookstoscrape.mapper;

import com.jonascarmo.bookstoscrape.dto.BookDto;
import com.jonascarmo.bookstoscrape.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "starRating.numberOfStars", target = "starRating")
    List<BookDto> bookListToBookDtoList(List<Book> book);

    @Mapping(source = "starRating.numberOfStars", target = "starRating")
    BookDto bookToBookDto(Book book);

    @Mapping(expression = "java(StarRating.getFromNumberOfStars(bookDto.getStarRating()))", target = "starRating")
    Book bookDtoToBook(BookDto bookDto);
}
