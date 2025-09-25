package org.upm.poo.domain;

import java.util.Objects;

public final class Product {
    private final int id;
    private String name;
    private Category category;
    private double price;

    public Product(int id, String name, Category category, double price) {
        if (id <= 0) throw new IllegalArgumentException("id must be > 0");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (category == null) throw new IllegalArgumentException("category required");
        if (price < 0) throw new IllegalArgumentException("price >= 0");
        this.id = id; this.name = name; this.category = category; this.price = price;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public double getPrice() { return price; }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        this.name = name;
    }
    public void setCategory(Category category) {
        if (category == null) throw new IllegalArgumentException("category required");
        this.category = category;
    }
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("price >= 0");
        this.price = price;
    }

    @Override public String toString() {
        return "{class:Product, id:" + id + ", name:'" + name + "', category:" + category + ", price:" + price + "}";
    }
    @Override public boolean equals(Object o){ return (o instanceof Product p) && p.id == id; }
    @Override public int hashCode(){ return Objects.hash(id); }
}
