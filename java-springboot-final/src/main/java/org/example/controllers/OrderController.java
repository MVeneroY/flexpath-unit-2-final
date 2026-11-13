package org.example.controllers;

import org.example.daos.OrderDao;
import org.example.models.Order;
import org.example.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderDao orderDao;

    @GetMapping
    public List<Order> getOrders() {
        return orderDao.getOrders();
    }

    @GetMapping(path = "/{id}")
    public Order getOrderById(@PathVariable("id") int orderId) {
        Order result =  orderDao.getOrderById(orderId);
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@RequestBody User user) {
        return orderDao.createOrder(user);
    }

    @PutMapping(path = "/{id}")
    public Order updateOrder(@PathVariable("id") int orderId, @RequestBody User user) {
        Order order = new Order(orderId, user.getUsername());
        try {
            return orderDao.updateOrder(order);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public int deleteOrder(@PathVariable("id") int orderId) {
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return orderDao.deleteOrder(order);
    }
}
