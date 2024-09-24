package com.jonascarmo.bookstoscrape.controller;

import com.jonascarmo.bookstoscrape.dto.BookDto;
import com.jonascarmo.bookstoscrape.dto.CategoryDto;
import com.jonascarmo.bookstoscrape.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BooksController {

    private final BookService bookService;

    @GetMapping(value={"", "/"})
    private ResponseEntity<List<BookDto>> findAll(@RequestParam(required = false, defaultValue = "10") int limit,
                                                  @RequestParam(required = false, defaultValue = "0") int offset) {
        List<BookDto> books = bookService.findAll(limit, offset);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    private ResponseEntity<BookDto> findById(@PathVariable String id) {
        BookDto category = findBook(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping(value={"", "/"})
    private ResponseEntity<Void> createBook(@RequestBody BookDto bookDto, UriComponentsBuilder ucb) {
        BookDto savedBook = bookService.save(bookDto);
        URI locationOfNewBook = ucb
                .path("books/{id}")
                .buildAndExpand(savedBook.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewBook).build();
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putBook(@PathVariable String requestedId, @RequestBody BookDto bookUpdate) {
        BookDto book = findBook(requestedId);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        bookUpdate.setId(book.getId());
        bookService.update(bookUpdate);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteBook(@PathVariable String id) {
        BookDto book = findBook(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private BookDto findBook(String id) {
        return bookService.findById(id);
    }

}
