package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.OrderItem;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class OrderItemDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderItemDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<OrderItem> getOrderItems() {
        return jdbcTemplate.query("SELECT * FROM order_items;", this::mapToOrderItem);
    }

    public OrderItem getOrderItemById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM order_items WHERE id = ?", this::mapToOrderItem, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity) values (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    con -> {
                        PreparedStatement prepStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        prepStmt.setInt(1, orderItem.getOrderId());
                        prepStmt.setInt(2, orderItem.getProductId());
                        prepStmt.setInt(3, orderItem.getQuantity());
                        return prepStmt;
                    },
                    keyHolder
            );
            int key = keyHolder.getKey().intValue();
            return getOrderItemById(key);
        } catch (DataAccessException e) {
            return null;
        }
    }

    public OrderItem updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, orderItem.getOrderId(), orderItem.getProductId(), orderItem.getQuantity(), orderItem.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected. expected at least one.");
        } else {
            return getOrderItemById(orderItem.getId());
        }
    }

    public int deleteOrderItem(OrderItem orderItem) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        return jdbcTemplate.update(sql, orderItem.getId());
    }

    private OrderItem mapToOrderItem(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        int orderId = resultSet.getInt("order_id");
        int productId = resultSet.getInt("product_id");
        int quantity = resultSet.getInt("quantity");
        return new OrderItem(
                id,
                orderId,
                productId,
                quantity
        );
    }
}
