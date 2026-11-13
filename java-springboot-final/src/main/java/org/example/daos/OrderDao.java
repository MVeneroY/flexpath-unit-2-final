package org.example.daos;

import org.example.exceptions.DaoException;
import org.example.models.Order;
import org.example.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

@Component
public class OrderDao {

    private final JdbcTemplate jdbcTemplate;

    public OrderDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Order> getOrders() {
        return jdbcTemplate.query("SELECT * FROM orders", this::mapToOrder);
    }

    public Order getOrderById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM orders WHERE id = ?", this::mapToOrder, id);
        } catch (EmptyResultDataAccessException e) {
            return  null;
        }
    }

//    public Order createOrder(User user) {
//        String sql = "INSERT INTO orders (username) VALUES (?);";
//        try (
//                Connection connection = jdbcTemplate.getDataSource().getConnection();
//                PreparedStatement preparedStatement = connection.prepareStatement(sql);
//                ) {
//            preparedStatement.setString(1, user.getUsername());
//            preparedStatement.executeUpdate();
//
//            try (ResultSet genKeys = preparedStatement.getGeneratedKeys()) {
//                if (genKeys.next()) {
//                    return getOrderById(genKeys.getInt(1));
//                }
//            }
//        } catch (SQLException e) {
//            throw new DaoException("Failed to create order.");
//        }
//    }

    public Order createOrder(User user) {
        String sql = "INSERT INTO orders (username) VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    con -> {
                        PreparedStatement prepStmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                        prepStmt.setString(1, user.getUsername());
                        return prepStmt;
                    },
                    keyHolder
            );
            System.out.println(keyHolder.getKey().intValue());
            return getOrderById(keyHolder.getKey().intValue());
        } catch (DataAccessException | NullPointerException e) {
            return null;
        }
    }

    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET username = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, order.getUsername(), order.getId());
        if (rowsAffected == 0) {
            throw new DaoException("Zero rows affected. expected at least one.");
        } else {
            return getOrderById(order.getId());
        }
    }

    public int deleteOrder(Order order) {
        String sql = "DELETE FROM orders WHERE id = ?";
        return jdbcTemplate.update(sql, order.getId());
    }

    private Order mapToOrder(ResultSet resultSet, int rowNumber) throws SQLException {
        int id = resultSet.getInt("id");
        String username = resultSet.getString("username");
        return new Order(
                id,
                username
        );
    }
}
