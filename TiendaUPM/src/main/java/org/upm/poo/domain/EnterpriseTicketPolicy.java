package org.upm.poo.domain;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

        if (p instanceof ServiceProduct sp) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
            String dateStr = sp.getExpiration().atStartOfDay(java.time.ZoneId.of("CET")).format(fmt);

            return "{class:ProductService, id:" + p.getId() + ", category:" + sp.getCategory() + ", expiration:" + dateStr + "}";
        }

        if (p instanceof EventProduct) {
            String base = p.toString();
            if (base.endsWith("}")) {
                base = base.substring(0, base.length() - 1);
            }
            return base + ", actual people in event:" + li.getQuantity() + "}";
        }

        String info = p.toString();
        if (!li.getCustomizations().isEmpty()) info += " " + li.getCustomizations();
        return info;
    }

    @Override
    public void printTicketInfo(Ticket t) {
        boolean hasServices = t.getItems().stream().anyMatch(li -> li.getProduct() instanceof ServiceProduct);

        if (hasServices) {
            System.out.println("Services Included: ");
            t.getItems().stream()
                    .filter(li -> li.getProduct() instanceof ServiceProduct)
                    .forEach(li -> {
                        String info = formatLineInfo(li);
                        for(int i=0; i<li.getQuantity(); i++) System.out.println("  " + info);
                    });
        }

        boolean hasProducts = t.getItems().stream().anyMatch(li -> !(li.getProduct() instanceof ServiceProduct));

        if (hasProducts) {
            if (hasServices) {
                System.out.println("Product Included");
            }

            t.getItems().stream()
                    .filter(li -> !(li.getProduct() instanceof ServiceProduct))
                    .forEach(li -> {
                        String info = formatLineInfo(li);
                        // En tickets de empresa NO se muestran descuentos línea a línea
                        for(int i=0; i<li.getQuantity(); i++) System.out.println("  " + info);
                    });

            double totalP = getProductSum(t);
            double disc = calculateTotalDiscount(t);

            System.out.println("  Total price: " + trim(totalP));

            if (disc > 0) {
                System.out.println("  Extra Discount from services:" + trim(disc) + " **discount -" + trim(disc));
                System.out.println("  Total discount: " + trim(disc));
            } else {
                System.out.println("  Total discount: 0.0");
            }
            System.out.println("  Final Price: " + trim(totalP - disc));
        } else {
            if (hasServices) {
                System.out.println("  Total price: 0.0");
                System.out.println("  Total discount: 0.0");
                System.out.println("  Final Price: 0.0");
            }
        }
    }

    @Override
    public void validateClosing(Ticket t) {
        boolean hasService = t.getItems().stream().anyMatch(li -> li.getProduct() instanceof ServiceProduct);
        boolean hasProduct = t.getItems().stream().anyMatch(li -> !(li.getProduct() instanceof ServiceProduct));

        if (hasProduct && !hasService) {
            throw new IllegalStateException("Enterprise tickets cannot contain only Products. Must include at least one Service.");
        }
    }

    private double getProductSum(Ticket t) {
        return t.getItems().stream()
                .filter(li -> !(li.getProduct() instanceof ServiceProduct))
                .mapToDouble(LineItem::subtotal)
                .sum();
    }

    private static String trim(double v) { return String.format(java.util.Locale.ROOT, "%.1f", v); }
}