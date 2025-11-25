package org.upm.poo.domain;

public abstract class ItemProduct extends Product {
    private Category category;

    public ItemProduct(String id, String name, double price, Category category) {
        super(id, name, price);
        setCategory(category);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        if(category == null) throw  new IllegalArgumentException("category required");
        this.category = category;
    }

    @Override
    public String toString() {
        return "{class:" + this.getClass().getSimpleName() +
                ", id:" + getId() +
                ", name:'" + getName() +
                "', category:" + category +
                ", price:" + getPrice() + "}";
    }
}