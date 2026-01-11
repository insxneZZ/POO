package org.upm.poo.cli;

import org.upm.poo.domain.*;

import java.util.Locale;

public class StandardTicketPrinter implements ITicketPrinter {

    @Override
    public void print(Ticket<?> t) {
        System.out.println("Ticket : " + t.getId());

        t.getItems().stream()
                .sorted((l1, l2) -> l1.getProduct().getName().compareToIgnoreCase(l2.getProduct().getName()))
                .forEach(li -> printLineItem(t, li));

        System.out.println("  Total price: " + trim(t.totalPrice()));
        System.out.println("  Total discount: " + trim(t.totalDiscount()));
        System.out.println("  Final Price: " + trim(t.finalPrice()));
    }

    private void printLineItem(Ticket<?> t, LineItem li) {
        Product p = li.getProduct();

        double base = p.getPrice();
        double extra = (!li.getCustomizations().isEmpty()) ? base * 0.10 * li.getCustomizations().size() : 0.0;
        double finalU = base + extra;

        double uDisc = 0.0;
        if (p instanceof ItemProduct ip) uDisc = t.unitDiscountFor(ip.getCategory(), finalU);

        for (int i = 0; i < li.getQuantity(); i++) {
            String info = p.toString();
            if (!li.getCustomizations().isEmpty()) info += " " + li.getCustomizations();

            if (uDisc > 0) System.out.println("  " + info + " **discount -" + trim(uDisc));
            else System.out.println("  " + info);
        }
    }

    private String trim(double v) {
        return String.format(Locale.ROOT, "%.1f", v);
    }
}