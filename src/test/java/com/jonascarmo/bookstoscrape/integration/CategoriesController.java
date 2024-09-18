package com.jonascarmo.bookstoscrape.integration;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jonascarmo.bookstoscrape.dto.CategoryDto;
import com.jonascarmo.bookstoscrape.model.Category;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CategoriesController {

    @Autowired
    TestRestTemplate restTemplate;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void shouldReturnAllCategoriesWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.getForEntity("/categories", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int categoryCount = documentContext.read("$.length()");
        assertThat(categoryCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder("0383342d-925c-4b3b-ac26-e2df7a27d44a",
                "6d64c451-d8e3-467f-a4b3-6dbabe151a81", "551f00b8-3d95-49d2-8a6c-7cdfbd948a27");

        JSONArray categories = documentContext.read("$..categoryName");
        assertThat(categories).containsExactlyInAnyOrder("Travel", "Mystery", "Historical Fiction");

        JSONArray urls = documentContext.read("$..categoryUrl");
        assertThat(urls).containsExactlyInAnyOrder("https://books.toscrape.com/catalogue/category/books/travel_2/index.html",
                "https://books.toscrape.com/catalogue/category/books/mystery_3/index.html",
                "https://books.toscrape.com/catalogue/category/books/historical-fiction_4/index.html");
    }

    @Test
    void shouldNotReturnACategoryWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/categories/3591534a-9c78-457e-9174-2c876a61a7d2", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnACategoryWithAKnownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/categories/0383342d-925c-4b3b-ac26-e2df7a27d44a", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        String id = documentContext.read("$.id");
        String name = documentContext.read("$.categoryName");
        String url = documentContext.read("$.categoryUrl");

        assertThat(id).isEqualTo("0383342d-925c-4b3b-ac26-e2df7a27d44a");
        assertThat(name).isEqualTo("Travel");
        assertThat(url).isEqualTo("https://books.toscrape.com/catalogue/category/books/travel_2/index.html");
    }

    @Test
    @DirtiesContext
    void shouldReturnAnEmptyArrayWhenThereIsNoCategory() {
        jdbcTemplate.execute("DELETE FROM categories");
        ResponseEntity<String> response = restTemplate.getForEntity("/categories", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int arrayLength = documentContext.read("$.length()");
        assertThat(arrayLength).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void shouldCreateANewCategory() {
        CategoryDto newCategory = new CategoryDto(null, "New category", "url.com");
        ResponseEntity<Void> createdResponse = restTemplate.postForEntity("/categories", newCategory, Void.class);
        assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewCategory = createdResponse.getHeaders().getLocation();
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCategory, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        String id = documentContext.read("$.id");
        String name = documentContext.read("$.categoryName");
        String url = documentContext.read("$.categoryUrl");

        assertThat(id).isNotNull();
        assertThat(name).isEqualTo("New category");
        assertThat(url).isEqualTo("url.com");
    }

    @Test
    @DirtiesContext
    void shouldUpdateAnExistingCategory() {
        CategoryDto categoryUpdate = new CategoryDto(null, "New category", "url.com");
        HttpEntity<CategoryDto> request = new HttpEntity<>(categoryUpdate);
        ResponseEntity<Void> response = restTemplate.exchange("/categories/0383342d-925c-4b3b-ac26-e2df7a27d44a",
                HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/categories/0383342d-925c-4b3b-ac26-e2df7a27d44a", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        String id = documentContext.read("$.id");
        String name = documentContext.read("$.categoryName");
        String url = documentContext.read("$.categoryUrl");

        assertThat(id).isEqualTo("0383342d-925c-4b3b-ac26-e2df7a27d44a");
        assertThat(name).isEqualTo("New category");
        assertThat(url).isEqualTo("url.com");
    }

    @Test
    void shouldNotUpdateACategoryThatDoesNotExist() {
        CategoryDto categoryUpdate = new CategoryDto(null, "New category", "url.com");
        HttpEntity<CategoryDto> request = new HttpEntity<>(categoryUpdate);
        ResponseEntity<Void> response = restTemplate.exchange("/categories/0383342d-925c-4b3b-ac26-e2df7a27d44b",
                HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void shouldDeleteAnExistingCategory() {
        ResponseEntity<Void> response = restTemplate.exchange("/categories/0383342d-925c-4b3b-ac26-e2df7a27d44a", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity("/categories/0383342d-925c-4b3b-ac26-e2df7a27d44a", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteACategoryThatDoesNotExist() {
        ResponseEntity<Void> response = restTemplate.exchange("/categories/0383342d-925c-4b3b-ac26-e2df7a27d44b", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}
