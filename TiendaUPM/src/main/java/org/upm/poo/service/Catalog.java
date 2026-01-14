package org.upm.poo.service;

import org.upm.poo.domain.*;
import java.util.*;

public final class Catalog {
    private final Map<String, Product> products = new LinkedHashMap<>();
    private int serviceCounter = 0;

    public Product add(Product p) {
        if (products.containsKey(p.getId()))
            throw new IllegalArgumentException("Product id already exists: " + p.getId());

        products.put(p.getId(), p);

        if(p.getId().matches("\\d+S")){
            try {
                int idNum = Integer.parseInt(p.getId().replace("S", ""));
                if(idNum > serviceCounter){
                    serviceCounter = idNum;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid product id: " + p.getId());
            }
        }
        return p;
    }

    public Collection<Product> list() { return products.values(); }

    public Product get(String id) {
        Product p = products.get(id);
        if (p == null) throw new NoSuchElementException("Product not found: " + id);
        return p;
    }

    public Product remove(String id) {
        Product removed = products.remove(id);
        if (removed == null)
            throw new NoSuchElementException("Product not found: " + id);
        return removed;
    }

    public String generateServiceId() {
        serviceCounter++;
        return serviceCounter + "S";
    }
}