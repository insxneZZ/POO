package org.upm.poo.domain;

import java.util.*;

public final class Ticket {
    private final List<LineItem> items = new ArrayList<>();
    private final DiscountPolicy policy = new CategoryQuantityDiscountPolicy();

    public void add(Product p, int q) {
        for (LineItem li : items) {
            if (li.getProduct().equals(p)) { li.add(q); return; }
        }
        items.add(new LineItem(p, q));
    }

    public void remove(int productId) {
        items.removeIf(li -> li.getProduct().getId() == productId);
    }

    public List<LineItem> getItems() { return Collections.unmodifiableList(items); }

    public double totalPrice()   { return policy.totalPrice(items); }
    public double totalDiscount(){ return policy.totalDiscount(items); }
    public double finalPrice()   { return totalPrice() - totalDiscount(); }

    public double unitDiscountFor(Category c, double unitPrice) {
        int catUnits = items.stream()
                .filter(li -> li.getProduct().getCategory() == c)
                .mapToInt(LineItem::getQuantity).sum();
        return policy.unitDiscount(c, unitPrice, catUnits);
    }
}
