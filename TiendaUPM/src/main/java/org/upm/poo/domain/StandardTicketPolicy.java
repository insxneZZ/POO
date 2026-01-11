package org.upm.poo.domain;

public class StandardTicketPolicy implements TicketPolicy {
    private final CategoryQuantityDiscountPolicy categoryPolicy = new CategoryQuantityDiscountPolicy();

    @Override
    public void checkAddition(Product p) {
        if (p instanceof ServiceProduct) {
            throw new IllegalArgumentException("Standard tickets cannot contain Services.");
        }
    }

    @Override
    public double calculateFinalPrice(Ticket t) {
        double total = t.totalPrice();
        double discount = calculateTotalDiscount(t);
        return total - discount;
    }

    @Override
    public double calculateTotalDiscount(Ticket t) {
        return categoryPolicy.totalDiscount(t.getItems());
    }

    @Override
    public String formatLineInfo(LineItem li) {
        Product p = li.getProduct();
        String info = p.toString();
        if (!li.getCustomizations().isEmpty()) info += " " + li.getCustomizations();

        return info;
    }
}