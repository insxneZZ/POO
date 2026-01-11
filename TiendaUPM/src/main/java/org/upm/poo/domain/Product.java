package org.upm.poo.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class Product implements Serializable {
    private String id;
    private String name;
    private double price;

    protected Product(String id, String name, double price) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID required");
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "class:" + getClass().getSimpleName() + ", id:" + id + ", name:'" + name + "', price:" + price;
    }
}