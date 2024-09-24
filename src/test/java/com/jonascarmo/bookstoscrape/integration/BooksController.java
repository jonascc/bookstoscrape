package com.jonascarmo.bookstoscrape.integration;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jonascarmo.bookstoscrape.dto.BookDto;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BooksController {

    @Autowired
    TestRestTemplate restTemplate;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void shouldReturnAllBooksWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int categoryCount = documentContext.read("$.length()");
        assertThat(categoryCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder("9b18c311-9100-43e2-a2e8-bdffeac2e12b",
                "9b18c311-9100-43e2-a2e8-bdffeac2e12c", "9b18c311-9100-43e2-a2e8-bdffeac2e12d");

        JSONArray categories = documentContext.read("$..name");
        assertThat(categories).containsExactlyInAnyOrder("It's Only the Himalayas", "It's Only the Himalayas 2", "It's Only the Himalayas 3");
    }

    @Test
    void shouldNotReturnABookWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books/3591534a-9c78-457e-9174-2c876a61a7d2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnABookWithAKnownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books/9b18c311-9100-43e2-a2e8-bdffeac2e12b", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String url = documentContext.read("$.url");

        assertThat(id).isEqualTo("9b18c311-9100-43e2-a2e8-bdffeac2e12b");
        assertThat(name).isEqualTo("It's Only the Himalayas");
        assertThat(url).isEqualTo("https://books.toscrape.com/catalogue/its-only-the-himalayas_981/index.html");
    }

    @Test
    @DirtiesContext
    void shouldReturnAnEmptyArrayWhenThereIsNoBook() {
        jdbcTemplate.execute("DELETE FROM books");
        ResponseEntity<String> response = restTemplate.getForEntity("/books", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int arrayLength = documentContext.read("$.length()");
        assertThat(arrayLength).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void shouldCreateANewBook() {
        BookDto newBook = new BookDto(null, "New book", "url.com", "", "",
                "", "", "", "0", "0", "0", 0, "", 1);
        ResponseEntity<Void> createdResponse = restTemplate.postForEntity("/books", newBook, Void.class);
        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewBook = createdResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewBook, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        String id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String url = documentContext.read("$.url");

        assertThat(id).isNotNull();
        assertThat(name).isEqualTo("New book");
        assertThat(url).isEqualTo("url.com");
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingBook() {
        BookDto bookUpdate = new BookDto(null, "New book", "url2.com", "", "",
                "", "", "", "0", "0", "0", 0, "", 1);
        HttpEntity<BookDto> request = new HttpEntity<>(bookUpdate);
        ResponseEntity<Void> response = restTemplate.exchange("/books/9b18c311-9100-43e2-a2e8-bdffeac2e12b",
                HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/books/9b18c311-9100-43e2-a2e8-bdffeac2e12b", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        String id = documentContext.read("$.id");
        String name = documentContext.read("$.name");
        String url = documentContext.read("$.url");

        assertThat(id).isEqualTo("9b18c311-9100-43e2-a2e8-bdffeac2e12b");
        assertThat(name).isEqualTo("New book");
        assertThat(url).isEqualTo("url2.com");
    }

    @Test
    void shouldNotUpdateABookThatDoesNotExist() {
        BookDto bookUpdate = new BookDto(null, "New book", "url2.com", "", "",
                "", "", "", "0", "0", "0", 0, "", 1);
        HttpEntity<BookDto> request = new HttpEntity<>(bookUpdate);
        ResponseEntity<Void> response = restTemplate.exchange("/books/0383342d-925c-4b3b-ac26-e2df7a27d44b",
                HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingBook() {
        ResponseEntity<Void> response = restTemplate.exchange("/books/9b18c311-9100-43e2-a2e8-bdffeac2e12b", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/books/9b18c311-9100-43e2-a2e8-bdffeac2e12b", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteABookThatDoesNotExist() {
        ResponseEntity<Void> response = restTemplate.exchange("/books/0383342d-925c-4b3b-ac26-e2df7a27d44b", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
