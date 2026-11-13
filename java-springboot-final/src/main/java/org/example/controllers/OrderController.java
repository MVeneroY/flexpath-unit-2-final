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
    public List<Order> getAll() {
        return orderDao.getOrders();
    }

    @GetMapping(path = "/{id}")
    public Order get(@PathVariable("id") int orderId) {
        Order order =  orderDao.getOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return order;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody User user) {
        return orderDao.createOrder(user);
    }

    @PutMapping(path = "/{id}")
    public Order update(@PathVariable("id") int orderId, @RequestBody User user) {
        Order order = new Order(orderId, user.getUsername());
        try {
            return orderDao.updateOrder(order);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable("id") int orderId) {
        Order order = orderDao.getOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return orderDao.deleteOrder(order);
    }
}
