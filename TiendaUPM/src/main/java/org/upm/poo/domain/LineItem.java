package org.upm.poo.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineItem implements Serializable {
    private final Product product;
    private int quantity;
    private final List<String> customizations;

    public LineItem(Product product, int quantity, List<String> customizations) {
        this.product = product;
        this.quantity = quantity;
        this.customizations = new ArrayList<>(customizations);
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public List<String> getCustomizations() { return Collections.unmodifiableList(customizations); }

    public void add(int q) { this.quantity += q; }

    public boolean represents(Product p, List<String> customs) {
        return this.product.equals(p) && this.customizations.equals(customs);
    }

    public double getUnitPrice() {
        double base = product.getPrice();
        double extra = (!customizations.isEmpty()) ? base * 0.10 * customizations.size() : 0.0;
        return base + extra;
    }

    public double getTotalPrice() {
        return getUnitPrice() * quantity;
    }
}