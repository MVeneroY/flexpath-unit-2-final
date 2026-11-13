package org.example.controllers;

import org.example.daos.OrderItemDao;
import org.example.models.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemDao orderItemDao;

    @GetMapping
    public List<OrderItem> getAll() {
        return orderItemDao.getOrderItems();
    }

    @GetMapping(path = "/{id}")
    public OrderItem get(@PathVariable("id") int orderItemId) {
        OrderItem orderItem = orderItemDao.getOrderItemById(orderItemId);
        if (orderItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return orderItem;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderItem create(@RequestBody OrderItem orderItem) {
        return orderItemDao.createOrderItem(orderItem);
    }

    @PutMapping(path = "/{id}")
    public OrderItem update(@PathVariable("id") int orderItemId, @RequestBody OrderItem orderItem) {
        orderItem.setId(orderItemId);
        try {
            return orderItemDao.updateOrderItem(orderItem);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable("id") int orderItemId) {
        OrderItem orderItem = orderItemDao.getOrderItemById(orderItemId);
        if (orderItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return orderItemDao.deleteOrderItem(orderItem);
    }
}
