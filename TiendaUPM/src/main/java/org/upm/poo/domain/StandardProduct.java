package org.upm.poo.domain;

public final class StandardProduct extends ItemProduct {
    public StandardProduct(String id, String name, Category category, double price) {
        super(id, name, price, category);
    }

    @Override
    public String toString() {
        return "{class:Product, id:" + getId() + ", name:'" + getName() + "', category:" + getCategory() + ", price:" + getPrice() + "}";
    }
}