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

    @Override
    public void validateClosing(Ticket t) {
    }

    @Override
    public void printTicketInfo(Ticket t) {
        t.getItems().stream()
                .sorted((l1, l2) -> l1.getProduct().getName().compareToIgnoreCase(l2.getProduct().getName()))
                .forEach(li -> {
                    Product p = li.getProduct();
                    String info = formatLineInfo(li);

                    double base = p.getPrice();
                    double extra = (!li.getCustomizations().isEmpty()) ? base * 0.10 * li.getCustomizations().size() : 0.0;
                    double finalU = base + extra;
                    double uDisc = 0.0;

                    if (p instanceof ItemProduct ip) {
                        CategoryQuantityDiscountPolicy cqdp = new CategoryQuantityDiscountPolicy();
                        int catUnits = t.getItems().stream()
                                .filter(l -> l.getProduct() instanceof ItemProduct ip2 && ip2.getCategory() == ip.getCategory())
                                .mapToInt(LineItem::getQuantity).sum();
                        uDisc = cqdp.unitDiscount(ip.getCategory(), finalU, catUnits);
                    }

                    for(int i = 0; i < li.getQuantity(); i++) {
                        if (uDisc > 0) System.out.println("  " + info + " **discount -" + String.format(java.util.Locale.ROOT, "%.1f", uDisc));
                        else System.out.println("  " + info);
                    }
                });

        System.out.println("  Total price: " + String.format(java.util.Locale.ROOT, "%.1f", t.totalPrice()));
        System.out.println("  Total discount: " + String.format(java.util.Locale.ROOT, "%.1f", calculateTotalDiscount(t)));
        System.out.println("  Final Price: " + String.format(java.util.Locale.ROOT, "%.1f", calculateFinalPrice(t)));
    }
}