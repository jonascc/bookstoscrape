package com.jonascarmo.bookstoscrape.dao;

import com.jonascarmo.bookstoscrape.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Repository
public class CategoryDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Category findById(String id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM categories WHERE id = ?",
                    (resultSet, rowNum) -> new Category(UUID.fromString(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("url")),
                    id);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    public List<Category> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM categories",
                (resultSet, rowNum) -> new Category(UUID.fromString(resultSet.getString("id")),
                        resultSet.getString("name"),
                        resultSet.getString("url"))
        );
    }

    public Category save(Category category) {
        jdbcTemplate.update("INSERT INTO categories (id, name, url) VALUES (?, ?, ?)",
                category.id(), category.name(), category.url());
        return category;
    }

    public void update(Category category) {
        jdbcTemplate.update("UPDATE categories SET name = ?, url = ? WHERE id = ?",
                category.name(), category.url(), category.id());
    }

    public void delete(String id) {
        jdbcTemplate.update("DELETE FROM categories WHERE id = ?", id);
    }

}
