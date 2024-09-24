package com.jonascarmo.bookstoscrape.dao;

import com.jonascarmo.bookstoscrape.enums.StarRating;
import com.jonascarmo.bookstoscrape.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public class BookDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Book> findAll(int limit, int offset) {
        return jdbcTemplate.query(
                "SELECT * FROM books LIMIT ? OFFSET ?",
                (resultSet, rowNum) -> new Book(UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("name"),
                        resultSet.getString("url"),
                        resultSet.getString("imgLink"),
                        null,
                        resultSet.getString("availability"),
                        resultSet.getString("description"),
                        resultSet.getString("upc"),
                        resultSet.getString("productType"),
                        new BigDecimal(resultSet.getString("priceExcludingTax")),
                        new BigDecimal(resultSet.getString("priceIncludingTax")),
                        new BigDecimal(resultSet.getString("tax")),
                        Integer.parseInt(resultSet.getString("numberOfReviews")),
                        resultSet.getString("currency"),
                        StarRating.getFromNumberOfStars(resultSet.getInt("starRating"))
                ), limit, offset
        );
    }

    public Book findById(String id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM books WHERE id = ?",
                    (resultSet, rowNum) -> new Book(UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("url"),
                            resultSet.getString("imgLink"),
                            null,
                            resultSet.getString("availability"),
                            resultSet.getString("description"),
                            resultSet.getString("upc"),
                            resultSet.getString("productType"),
                            new BigDecimal(resultSet.getString("priceExcludingTax")),
                            new BigDecimal(resultSet.getString("priceIncludingTax")),
                            new BigDecimal(resultSet.getString("tax")),
                            Integer.parseInt(resultSet.getString("numberOfReviews")),
                            resultSet.getString("currency"),
                            StarRating.getFromNumberOfStars(resultSet.getInt("starRating"))
                    ), id
            );
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public Book save(Book book) {
        jdbcTemplate.update(
                "INSERT INTO books VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                book.getId(),
                book.getName(),
                book.getUrl(),
                book.getImgLink(),
                book.getAvailability(),
                book.getDescription(),
                book.getUpc(),
                book.getProductType(),
                book.getPriceExcludingTax(),
                book.getPriceIncludingTax(),
                book.getTax(),
                book.getNumberOfReviews(),
                book.getCurrency(),
                book.getStarRating().getNumberOfStars()
        );
        return book;
    }

    public void update(Book book) {
        jdbcTemplate.update(
                "UPDATE books SET name = ?, url = ?, imgLink = ?, availability = ?, " +
                        "description = ?, upc = ?, productType = ?, priceExcludingTax = ?, " +
                        "priceIncludingTax = ?, tax = ?, numberOfReviews = ?, currency = ?, " +
                        "starRating = ? WHERE id = ?",
                book.getName(),
                book.getUrl(),
                book.getImgLink(),
                book.getAvailability(),
                book.getDescription(),
                book.getUpc(),
                book.getProductType(),
                book.getPriceExcludingTax(),
                book.getPriceIncludingTax(),
                book.getTax(),
                book.getNumberOfReviews(),
                book.getCurrency(),
                book.getStarRating().getNumberOfStars(),
                book.getId()
        );
    }

    public void delete(String id) {
        jdbcTemplate.update("DELETE FROM books WHERE id = ?", id);
    }

}
