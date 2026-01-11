package org.upm.poo.domain;

import java.util.List;

public class EnterpriseTicketPolicy implements TicketPolicy {

    @Override
    public void checkAddition(Product p) {
    }

    @Override
    public double calculateFinalPrice(Ticket t) {
        double productTotal = getProductSum(t);
        double discount = calculateTotalDiscount(t);
        return productTotal - discount;
    }

    @Override
    public double calculateTotalDiscount(Ticket t) {
        long serviceCount = t.getItems().stream()
                .filter(li -> li.getProduct() instanceof ServiceProduct)
                .mapToLong(LineItem::getQuantity)
                .sum();

        if (serviceCount == 0) return 0.0;

        double productTotal = getProductSum(t);
        double discountRate = 0.15 * serviceCount;

        if (discountRate > 1.0) discountRate = 1.0;

        return productTotal * discountRate;
    }

    @Override
    public String formatLineInfo(LineItem li) {
        Product p = li.getProduct();

        if (p instanceof ServiceProduct) {
            return "{class:ServiceProduct, id:" + p.getId() + ", expiration:" + ((ServiceProduct)p).getExpiration() + "} (Price deferred)";
        }

        String info = p.toString();
        if (!li.getCustomizations().isEmpty()) info += " " + li.getCustomizations();
        return info;
    }

    private double getProductSum(Ticket t) {
        return t.getItems().stream()
                .filter(li -> !(li.getProduct() instanceof ServiceProduct))
                .mapToDouble(LineItem::subtotal)
                .sum();
    }
}