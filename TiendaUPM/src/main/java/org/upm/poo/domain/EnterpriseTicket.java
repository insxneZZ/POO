package org.upm.poo.domain;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EnterpriseTicket extends Ticket {

    public EnterpriseTicket(String id, String cashierId, String clientId) {
        super(id, cashierId, clientId);
    }

    @Override
    public void checkAddition(Product p) {
    }

    @Override
    public double calculateTotalDiscount() {
        long serviceCount = items.stream()
                .filter(li -> li.getProduct() instanceof ServiceProduct)
                .mapToLong(LineItem::getQuantity)
                .sum();

        if (serviceCount == 0) return 0.0;

        double productTotal = getProductSum();
        double discountRate = 0.15 * serviceCount;

        if (discountRate > 1.0) discountRate = 1.0;

        return productTotal * discountRate;
    }

    @Override
    public double calculateFinalPrice() {
        double productTotal = getProductSum();
        return productTotal - calculateTotalDiscount();
    }

    private double getProductSum() {
        return items.stream()
                .filter(li -> !(li.getProduct() instanceof ServiceProduct))
                .mapToDouble(LineItem::subtotal)
                .sum();
    }

    @Override
    public void validateClosing() {
        boolean hasService = items.stream().anyMatch(li -> li.getProduct() instanceof ServiceProduct);
        boolean hasProduct = items.stream().anyMatch(li -> !(li.getProduct() instanceof ServiceProduct));

        if (hasProduct && !hasService) {
            throw new IllegalStateException("Enterprise tickets cannot contain only Products. Must include at least one Service.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Ticket : " + getId());

        boolean hasServices = items.stream().anyMatch(li -> li.getProduct() instanceof ServiceProduct);

        if (hasServices) {
            System.out.println("Services Included: ");
            items.stream()
                    .filter(li -> li.getProduct() instanceof ServiceProduct)
                    .forEach(li -> {
                        String info = formatServiceInfo((ServiceProduct) li.getProduct());
                        for(int i=0; i<li.getQuantity(); i++) System.out.println("  " + info);
                    });
        }

        boolean hasProducts = items.stream().anyMatch(li -> !(li.getProduct() instanceof ServiceProduct));

        if (hasProducts) {
            if (hasServices) System.out.println("Product Included");

            items.stream()
                    .filter(li -> !(li.getProduct() instanceof ServiceProduct))
                    .forEach(li -> {
                        String info = li.toString();
                        for(int i=0; i<li.getQuantity(); i++) System.out.println("  " + info);
                    });

            double totalP = getProductSum();
            double disc = calculateTotalDiscount();

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

    private String formatServiceInfo(ServiceProduct sp) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        String dateStr = sp.getExpiration().atStartOfDay(java.time.ZoneId.of("CET")).format(fmt);
        return "{class:ProductService, id:" + sp.getId() + ", category:" + sp.getCategory() + ", expiration:" + dateStr + "}";
    }
}