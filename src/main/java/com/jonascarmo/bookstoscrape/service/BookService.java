package com.jonascarmo.bookstoscrape.service;

import com.jonascarmo.bookstoscrape.dao.BookDao;
import com.jonascarmo.bookstoscrape.dto.BookDto;
import com.jonascarmo.bookstoscrape.mapper.BookMapper;
import com.jonascarmo.bookstoscrape.model.Book;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookDao bookDao;

    private final BookMapper mapper = Mappers.getMapper(BookMapper.class);

    public List<BookDto> findAll(int limit, int offset) {
        List<Book> books = bookDao.findAll(limit, offset);
        return mapper.bookListToBookDtoList(books);
    }

    public BookDto findById(String id) {
        Book book = bookDao.findById(id);
        return book != null ? mapper.bookToBookDto(book) : null;
    }

    public BookDto save(BookDto bookDto) {
        bookDto.setId(UUID.randomUUID());
        Book savedBook = bookDao.save(mapper.bookDtoToBook(bookDto));
        return mapper.bookToBookDto(savedBook);
    }

    public void update(BookDto bookUpdate) {
        bookDao.update(mapper.bookDtoToBook(bookUpdate));
    }

    public void delete(String id) {
        bookDao.delete(id);
    }
}
