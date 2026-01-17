package org.upm.poo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineItem {
    private final Product product;
    private int quantity;
    private final List<String> customizations;

    public LineItem(Product product, int quantity, List<String> customizations) {
        this.product = product;
        this.quantity = quantity;
        this.customizations = new ArrayList<>(customizations);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public List<String> getCustomizations() {
        return customizations;
    }

    public void add(int q) {
        this.quantity += q;
    }

    public double subtotal() {
        double unitPrice = product.getPrice();

        if (product instanceof CustomizableProduct && !customizations.isEmpty()) {
            unitPrice += (product.getPrice() * 0.10 * customizations.size());
        }

        return unitPrice * quantity;
    }


    public boolean represents(Product p, List<String> customs) {
        if (!this.product.equals(p)) return false;
        if (this.customizations.size() != customs.size()) return false;
        return this.customizations.containsAll(customs) && customs.containsAll(this.customizations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineItem lineItem)) return false;
        return quantity == lineItem.quantity &&
                Objects.equals(product, lineItem.product) &&
                Objects.equals(customizations, lineItem.customizations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, quantity, customizations);
    }

    @Override
    public String toString() {
        String info = product.toString();
        if (!customizations.isEmpty()) {
            info += " " + customizations;
        }
        return info;
    }
}