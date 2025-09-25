package org.upm.poo.domain;

import java.util.*;
import java.util.stream.Collectors;

public final class CategoryQuantityDiscountPolicy implements DiscountPolicy {
    private static final Map<Category, Double> PCT = Map.of(
            Category.MERCH, 0.00,
            Category.STATIONERY, 0.05,
            Category.CLOTHES, 0.07,
            Category.BOOK, 0.10,
            Category.ELECTRONICS, 0.03
    );

    @Override public double totalPrice(List<LineItem> items) {
        return items.stream().mapToDouble(LineItem::subtotal).sum();
    }

    @Override public double totalDiscount(List<LineItem> items) {
        Map<Category, Integer> units = items.stream()
                .collect(Collectors.groupingBy(li -> li.getProduct().getCategory(),
                        Collectors.summingInt(LineItem::getQuantity)));

        double disc = 0.0;
        for (LineItem li : items) {
            int catUnits = units.getOrDefault(li.getProduct().getCategory(), 0);
            double u = unitDiscount(li.getProduct().getCategory(), li.getProduct().getPrice(), catUnits);
            disc += u * li.getQuantity();
        }
        return disc;
    }

    @Override public double unitDiscount(Category cat, double unitPrice, int totalUnitsOfCategory) {
        double pct = (totalUnitsOfCategory >= 2) ? PCT.getOrDefault(cat, 0.0) : 0.0;
        return unitPrice * pct;
    }
}
