package org.upm.poo.domain;

import java.util.Objects;

public class Product {
    private final String id;
    private String name;
    private double price;

    public Product(String id, String name, double price) {
        if (id == null) throw new IllegalArgumentException("id required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (price < 0) throw new IllegalArgumentException("price >= 0");
        this.id = id; this.name = name; this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getType() {
        return this.getClass().getSimpleName();
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        this.name = name;
    }
    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException("price >= 0");
        this.price = price;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id);
    }

    @Override public int hashCode() {
        return Objects.hash(id);
    }

    @Override public String toString() {
        return "{class:" + this.getClass().getSimpleName() + ", id:" + id + ", name:'" + name + "', price:" + price + "}";
    }
}
