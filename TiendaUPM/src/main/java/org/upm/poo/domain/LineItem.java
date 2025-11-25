package org.upm.poo.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LineItem {
    private final Product product;
    private int quantity;
    private final List<String> customizations;

    public LineItem(Product product, int quantity, List<String> customizations) {
        if (product == null) throw new IllegalArgumentException("product required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.product = product;
        this.quantity = quantity;
        this.customizations = (customizations == null) ? List.of() : new ArrayList<>(customizations);
    }

    public LineItem(Product product, int quantity) {
        this(product, quantity, List.of());
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public List<String> getCustomizations() { return Collections.unmodifiableList(customizations); }

    public void add(int q) {
        if (q <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity += q;
    }

    public double subtotal() {
        double unitPrice = product.getPrice();

        if (!customizations.isEmpty()) {
            double surcharge = unitPrice * 0.10 * customizations.size();
            unitPrice += surcharge;
        }

        return unitPrice * quantity;
    }

    public boolean represents(Product p, List<String> customs) {
        if (!this.product.equals(p)) return false;

        if (this.customizations.size() != customs.size()) return false;
        return this.customizations.equals(customs);
    }
}