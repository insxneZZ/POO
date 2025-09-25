package org.upm.poo.domain;

import java.util.List;

public interface DiscountPolicy {
    double totalPrice(List<LineItem> items);
    double totalDiscount(List<LineItem> items);
    double unitDiscount(Category category, double unitPrice, int totalUnitsOfCategory);
}
