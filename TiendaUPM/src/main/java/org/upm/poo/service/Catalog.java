package org.upm.poo.service;

import org.upm.poo.domain.Product;

import java.util.*;

public final class Catalog {
    private final Map<Integer, Product> products = new LinkedHashMap<>();

    public Product add(Product p) {
        if (products.containsKey(p.getId()))
            throw new IllegalArgumentException("Product id already exists: " + p.getId());
        products.put(p.getId(), p);
        return p;
    }

    public Collection<Product> list() { return products.values(); }

    public Product get(int id) {
        Product p = products.get(id);
        if (p == null) throw new NoSuchElementException("Product not found: " + id);
        return p;
    }

    public Product remove(int id) {
        Product removed = products.remove(id);
        if (removed == null)
            throw new NoSuchElementException("Product not found: " + id);
        return removed;
    }
}
