package org.upm.poo.domain;

public final class StandardProduct extends ItemProduct {
    public StandardProduct(String id, String name, Category category, double price) {
        super(id, name, price, category);
    }
}