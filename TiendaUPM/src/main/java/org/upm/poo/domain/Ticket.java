package org.upm.poo.domain;

import org.upm.poo.domain.user.Customer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class Ticket<T extends Customer> implements Serializable {
    private String id;
    private final String cashierId;
    private final T customer;
    private TicketState state;
    protected final List<LineItem> items = new ArrayList<>();
    private static final DateTimeFormatter ID_FMT = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");

    protected Ticket(String id, String cashierId, T customer) {
        if (cashierId == null || cashierId.isBlank()) throw new IllegalArgumentException("Cashier ID required");
        if (customer == null) throw new IllegalArgumentException("Customer required");

        this.cashierId = cashierId;
        this.customer = customer;
        this.state = TicketState.EMPTY;
        this.id = (id == null || id.isBlank()) ? generateInitialId() : id;
    }

    protected String generateInitialId() {
        String datePart = LocalDateTime.now().format(ID_FMT);
        int randomPart = 10000 + new Random().nextInt(90000);
        return datePart + "-" + randomPart;
    }

    public String getId() { return id; }
    public String getCashierId() { return cashierId; }
    public T getCustomer() { return customer; }
    public TicketState getState() { return state; }
    public List<LineItem> getItems() { return Collections.unmodifiableList(items); }

    public abstract void add(Product p, int q, List<String> customizations);

    protected void addInternal(Product p, int q, List<String> customizations) {
        if (state == TicketState.CLOSED) {
            throw new IllegalStateException("Cannot add products to a CLOSED ticket");
        }

        if (p instanceof EventProduct) {
            boolean exists = items.stream().anyMatch(li -> li.getProduct().equals(p));
            if (exists) {
                throw new IllegalArgumentException("Event/Food product " + p.getId() + " is already in the ticket");
            }
        }

        if (p instanceof CustomizableProduct cp) {
            if (customizations != null && customizations.size() > cp.getMaxCustomizations()) {
                throw new IllegalArgumentException("Too many customizations. Max allowed: " + cp.getMaxCustomizations());
            }
        } else {
            if (customizations != null && !customizations.isEmpty()) {
                throw new IllegalArgumentException("Product " + p.getName() + " does not support customization");
            }
        }

        List<String> safeCustoms = (customizations == null) ? List.of() : customizations;

        for (LineItem li : items) {
            if (li.represents(p, safeCustoms)) {
                li.add(q);
                updateState();
                return;
            }
        }

        items.add(new LineItem(p, q, safeCustoms));
        updateState();
    }

    public void remove(String productId) {
        if (state == TicketState.CLOSED) {
            throw new IllegalStateException("Cannot remove products from a CLOSED ticket");
        }
        items.removeIf(li -> li.getProduct().getId().equals(productId));
        updateState();
    }

    public void close() {
        if (state == TicketState.CLOSED) return;

        checkCloseConditions();

        LocalDateTime now = LocalDateTime.now();
        for (LineItem li : items) {
            if (li.getProduct() instanceof EventProduct ep) {
                ep.validatePlanningTime(now);
            }
        }

        this.state = TicketState.CLOSED;
        String closeSuffix = LocalDateTime.now().format(ID_FMT);
        this.id = this.id + "-" + closeSuffix;
    }

    protected abstract void checkCloseConditions();

    private void updateState() {
        if (state == TicketState.CLOSED) return;
        this.state = items.isEmpty() ? TicketState.EMPTY : TicketState.ACTIVE;
    }

    public abstract double totalPrice();
    public abstract double totalDiscount();
    public double finalPrice() { return totalPrice() - totalDiscount(); }

    public abstract double unitDiscountFor(Category c, double unitPrice);
}