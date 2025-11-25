package org.upm.poo.domain;

public final class CustomizableProduct extends ItemProduct {
    private final int maxCustomizations;

    public CustomizableProduct(String id, String name, Category category, double price, int maxCustomizations) {
        super(id, name, price, category);
        if (maxCustomizations < 0) throw new IllegalArgumentException("maxCustomizations must be >= 0");
        this.maxCustomizations = maxCustomizations;
    }

    public int getMaxCustomizations() { return maxCustomizations; }

    @Override
    public String toString() {
        return "{class:ProductPersonalized, id:" + getId() + ", name:'" + getName() + "', category:" + getCategory() +
                ", price:" + getPrice() + ", maxPersonal:" + getMaxCustomizations() + "}";
    }
}