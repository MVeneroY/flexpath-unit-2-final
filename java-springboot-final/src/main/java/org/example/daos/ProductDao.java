package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Product;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProductDao {

    private final JdbcTemplate jdbcTemplate;

    public ProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Product> getProducts() {
        return jdbcTemplate.query("SELECT * FROM products;", this::mapToProduct);
    }

    public Product getProductById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?", this::mapToProduct, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Product createProduct(Product product) {
        String sql = "INSERT INTO products (name, price) values (?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    con -> {
                        PreparedStatement prepStmt = con.prepareStatement(sql);
                        prepStmt.setString(1, product.getName());
                        prepStmt.setBigDecimal(2, product.getPrice());
                        return prepStmt;
                    },
                    keyHolder
            );
            int key = keyHolder.getKey().intValue();
            return getProductById(key);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public Product updateProduct(Product product) {
        String sql = "UPDATE product SET name = ?, price = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, product.getName(), product.getPrice(), product.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected. expected at least one.");
        } else {
            return getProductById(product.getId());
        }
    }

    public int deleteProduct(Product product) {
        String sql = "DELETE FROM products WHERE id = ?";
        return jdbcTemplate.update(sql, product.getId());
    }

    private Product mapToProduct(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        BigDecimal price = resultSet.getBigDecimal("price");
        return new Product(
                id,
                name,
                price
        );
    }


}
