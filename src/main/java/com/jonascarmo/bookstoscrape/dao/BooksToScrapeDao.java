package com.jonascarmo.bookstoscrape.dao;

import com.jonascarmo.bookstoscrape.model.Book;
import com.jonascarmo.bookstoscrape.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BooksToScrapeDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createCategoriesTable() {
        jdbcTemplate.execute("DROP TABLE categories IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE categories(" +
                "id UUID PRIMARY KEY, name VARCHAR(50), url VARCHAR(255))");
    }

    public void createBooksTable() {
        jdbcTemplate.execute("DROP TABLE books IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE books(" +
                "id UUID PRIMARY KEY, name VARCHAR(255), url VARCHAR(255), imgLink VARCHAR(255)," +
                "availability VARCHAR(50), description CLOB(10K), upc VARCHAR(20), productType VARCHAR(5)," +
                "priceExcludingTax NUMERIC(5,2), priceIncludingTax NUMERIC(5,2), tax NUMERIC(5,2), numberOfReviews INT," +
                "currency CHAR, starRating INT)");
    }

    public void insertCategories(List<Category> scrapedCategories) {
        List<Object[]> elements = scrapedCategories
                .stream()
                .map(category -> new Object[]{category.id(), category.name(), category.url()}).toList();
        jdbcTemplate.batchUpdate("INSERT INTO categories(id, name, url) values (?, ?, ?)", elements);
    }

    public void insertBooks(List<Book> books) {
        List<Object[]> elements = books
                .stream()
                .map(book -> new Object[]{book.getId(), book.getName(), book.getUrl(), book.getImgLink(),
                book.getAvailability(), book.getDescription(), book.getUpc(), book.getProductType(),
                book.getPriceExcludingTax(), book.getPriceIncludingTax(), book.getTax(),
                book.getNumberOfReviews(), book.getCurrency(), book.getStarRating().getNumberOfStars()}).toList();
        jdbcTemplate.batchUpdate("INSERT INTO books(id, name, url, imgLink, " +
                "availability, description, upc, productType, priceExcludingTax, " +
                "priceIncludingTax, tax, numberOfReviews, currency, starRating) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", elements);
    }

}
