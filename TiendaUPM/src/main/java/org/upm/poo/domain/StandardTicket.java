package org.upm.poo.domain;

public class StandardTicket extends Ticket {

    public StandardTicket(String id, String cashierId, String clientId) {
        super(id, cashierId, clientId);
    }

    @Override
    public void checkAddition(Product p) {
        if (p instanceof ServiceProduct) {
            throw new IllegalArgumentException("Standard tickets cannot contain Services");
        }
    }

    @Override
    public double calculateTotalDiscount() {
        double totalDiscount = 0.0;
        CategoryQuantityDiscountPolicy policy = new CategoryQuantityDiscountPolicy();

        for (LineItem li : items) {
            if (li.getProduct() instanceof ItemProduct ip) {
                int catCount = items.stream()
                        .filter(i -> i.getProduct() instanceof ItemProduct p && p.getCategory() == ip.getCategory())
                        .mapToInt(LineItem::getQuantity)
                        .sum();

                double disc = policy.unitDiscount(ip.getCategory(), ip.getPrice(), catCount);
                totalDiscount += disc * li.getQuantity();
            }
        }
        return totalDiscount;
    }

    @Override
    public double calculateFinalPrice() {
        return totalPrice() - calculateTotalDiscount();
    }

    @Override
    public void validateClosing() {

    }

    @Override
    public void printDetails() {
        System.out.println("Ticket : " + getId());

        CategoryQuantityDiscountPolicy policy = new CategoryQuantityDiscountPolicy();

        for (LineItem li : items) {
            String info = li.toString();
            if (li.getProduct() instanceof ItemProduct ip) {
                int catCount = items.stream()
                        .filter(i -> i.getProduct() instanceof ItemProduct p && p.getCategory() == ip.getCategory())
                        .mapToInt(LineItem::getQuantity).sum();

                double unitDisc = policy.unitDiscount(ip.getCategory(), ip.getPrice(), catCount);
                if (unitDisc > 0) {
                    info += " **discount -" + trim(unitDisc);
                }
            }

            for(int i=0; i < li.getQuantity(); i++) {
                System.out.println("  " + info);
            }
        }

        System.out.println("  Total price: " + trim(totalPrice()));
        System.out.println("  Total discount: " + trim(calculateTotalDiscount()));
        System.out.println("  Final Price: " + trim(calculateFinalPrice()));
    }
}