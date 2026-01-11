package org.upm.poo.domain;

import org.upm.poo.domain.user.Client;
import java.util.List;

public class StandardTicket extends Ticket<Client> {
    private final DiscountPolicy policy = new CategoryQuantityDiscountPolicy();

    public StandardTicket(String id, String cashierId, Client client) {
        super(id, cashierId, client);
    }

    @Override
    public void add(Product p, int q, List<String> customizations) {
        if (p instanceof Service) {
            throw new IllegalArgumentException("Standard tickets cannot accept Services");
        }
        super.addInternal(p, q, customizations);
    }

    @Override
    protected void checkCloseConditions() {
    }

    @Override
    public double totalPrice() {
        return policy.totalPrice(items);
    }

    @Override
    public double totalDiscount() {
        return policy.totalDiscount(items);
    }

    @Override
    public double unitDiscountFor(Category c, double unitPrice) {
        int catUnits = items.stream()
                .filter(li -> li.getProduct() instanceof ItemProduct ip && ip.getCategory() == c)
                .mapToInt(LineItem::getQuantity).sum();
        return policy.unitDiscount(c, unitPrice, catUnits);
    }
}