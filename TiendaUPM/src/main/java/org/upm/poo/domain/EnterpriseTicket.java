package org.upm.poo.domain;

import org.upm.poo.domain.user.Company;
import java.util.List;

public class EnterpriseTicket extends Ticket<Company> {
    public enum Mode { SERVICE_ONLY, COMBINED }

    private final Mode mode;
    private final DiscountPolicy basePolicy = new CategoryQuantityDiscountPolicy();

    public EnterpriseTicket(String id, String cashierId, Company company, Mode mode) {
        super(id, cashierId, company);
        this.mode = mode;
    }

    public Mode getMode() { return mode; }

    @Override
    public void add(Product p, int q, List<String> customizations) {
        boolean isService = (p instanceof Service);

        if (mode == Mode.SERVICE_ONLY && !isService) {
            throw new IllegalArgumentException("Service-Only tickets cannot accept standard Products");
        }

        super.addInternal(p, q, customizations);
    }

    @Override
    protected void checkCloseConditions() {
        if (mode == Mode.COMBINED) {
            boolean hasProduct = items.stream().anyMatch(li -> !(li.getProduct() instanceof Service));
            boolean hasService = items.stream().anyMatch(li -> li.getProduct() instanceof Service);

            if (!hasProduct || !hasService) {
                throw new IllegalStateException("Combined tickets must have at least one Product and one Service to close");
            }
        }
    }

    @Override
    public double totalPrice() {
        if (mode == Mode.SERVICE_ONLY) return 0.0;

        return items.stream()
                .filter(li -> !(li.getProduct() instanceof Service))
                .mapToDouble(LineItem::getTotalPrice)
                .sum();
    }

    @Override
    public double totalDiscount() {
        if (mode == Mode.SERVICE_ONLY) return 0.0;

        double baseDiscount = items.stream()
                .filter(li -> !(li.getProduct() instanceof Service))
                .mapToDouble(li -> {
                    if (li.getProduct() instanceof ItemProduct ip) {
                        return unitDiscountFor(ip.getCategory(), li.getUnitPrice()) * li.getQuantity();
                    }
                    return 0.0;
                }).sum();

        long serviceCount = items.stream()
                .filter(li -> li.getProduct() instanceof Service)
                .mapToInt(LineItem::getQuantity)
                .sum();

        double productTotal = totalPrice();
        double servicePlusDiscount = productTotal * 0.15 * serviceCount;

        return baseDiscount + servicePlusDiscount;
    }

    @Override
    public double unitDiscountFor(Category c, double unitPrice) {
        int catUnits = items.stream()
                .filter(li -> li.getProduct() instanceof ItemProduct ip && ip.getCategory() == c)
                .mapToInt(LineItem::getQuantity).sum();
        return basePolicy.unitDiscount(c, unitPrice, catUnits);
    }
}