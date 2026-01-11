package org.upm.poo.domain;

import java.io.Serializable;
import java.util.List;

public interface DiscountPolicy extends Serializable {
    double totalPrice(List<LineItem> items);
    double totalDiscount(List<LineItem> items);
    double unitDiscount(Category category, double unitPrice, int categoryQuantity);
}