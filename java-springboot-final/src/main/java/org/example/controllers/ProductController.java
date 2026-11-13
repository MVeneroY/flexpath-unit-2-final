package org.example.controllers;

import org.example.daos.ProductDao;
import org.example.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductDao productDao;

    @GetMapping
    public List<Product> getAll() {
        return productDao.getProducts();
    }

    @GetMapping(path = "/{id}")
    public Product get(@PathVariable("id") int productId) {
        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return product;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody Product product) {
        return productDao.createProduct(product);
    }

    @PutMapping(path = "/{id}")
    public Product update(@PathVariable("id") int productId, @RequestBody Product product) {
        product.setId(productId);
        try {
            return productDao.updateProduct(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(path = "/{id}")
    public int delete(@PathVariable("id") int productId) {
        Product product = productDao.getProductById(productId);
        if (product == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return productDao.deleteProduct(product);
    }
}
