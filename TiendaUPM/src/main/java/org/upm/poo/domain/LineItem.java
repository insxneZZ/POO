package org.upm.poo.domain;

public final class LineItem {
    private final Product product;
    private int quantity;

    public LineItem(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("product required");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.product = product; this.quantity = quantity;
    }
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void add(int q) {
        if (q <= 0) throw new IllegalArgumentException("quantity must be > 0");
        this.quantity += q;
    }
    public double subtotal() { return product.getPrice() * quantity; }
}
