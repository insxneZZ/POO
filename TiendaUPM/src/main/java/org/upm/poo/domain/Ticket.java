package org.upm.poo.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public abstract class Ticket {
    protected String id;
    protected final String cashierId;
    protected final String clientId;
    protected TicketState state;
    protected final List<LineItem> items = new ArrayList<>();

    private static final DateTimeFormatter ID_FMT = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");

    public Ticket(String id, String cashierId, String clientId) {
        if (cashierId == null || cashierId.isBlank()) throw new IllegalArgumentException("Cashier ID required");
        if (clientId == null || clientId.isBlank()) throw new IllegalArgumentException("Client ID required");

        this.cashierId = cashierId;
        this.clientId = clientId;
        this.state = TicketState.EMPTY;
        this.id = (id == null || id.isBlank()) ? generateInitialId() : id;
    }

    // --- MÉTODOS PARA PERSISTENCIA ---

    public void loadFromPersistence(TicketState loadedState, List<LineItem> loadedItems) {
        this.state = loadedState;
        this.items.clear();
        if (loadedItems != null) {
            this.items.addAll(loadedItems);
        }
    }

    // --- MÉTODOS ABSTRACTOS ---
    public abstract void checkAddition(Product p);
    public abstract double calculateTotalDiscount();
    public abstract double calculateFinalPrice();
    public abstract void validateClosing();
    public abstract void printDetails();

    // --- LÓGICA COMÚN ---
    private String generateInitialId() {
        String datePart = LocalDateTime.now().format(ID_FMT);
        int randomPart = 10000 + new Random().nextInt(90000);
        return datePart + "-" + randomPart;
    }

    public String getId() { return id; }
    public String getCashierId() { return cashierId; }
    public String getClientId() { return clientId; }
    public TicketState getState() { return state; }
    public List<LineItem> getItems() { return Collections.unmodifiableList(items); }

    public void add(Product p, int q, List<String> customizations) {
        if (state == TicketState.CLOSED) {
            throw new IllegalStateException("Cannot add products to a CLOSED ticket");
        }

        checkAddition(p);

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
        boolean found = false;

        for (LineItem li : items) {
            if (li.represents(p, safeCustoms)) {
                li.add(q);
                found = true;
                break;
            }
        }

        if (!found) {
            items.add(new LineItem(p, q, safeCustoms));
        }
        updateState();
    }

    public void add(Product p, int q) {
        add(p, q, List.of());
    }

    public void remove(String productId) {
        if (state == TicketState.CLOSED) throw new IllegalStateException("Cannot remove products from a CLOSED ticket");
        items.removeIf(li -> li.getProduct().getId().equals(productId));
        updateState();
    }

    public void close() {
        if (state == TicketState.CLOSED) return;

        LocalDateTime now = LocalDateTime.now();
        for (LineItem li : items) {
            if (li.getProduct() instanceof EventProduct ep) {
                ep.validatePlanningTime(now);
            }
        }

        validateClosing();
        this.state = TicketState.CLOSED;
        this.id = this.id + "-" + LocalDateTime.now().format(ID_FMT);
    }

    protected void updateState() {
        if (state == TicketState.CLOSED) return;
        this.state = items.isEmpty() ? TicketState.EMPTY : TicketState.ACTIVE;
    }

    public double totalPrice() {
        return items.stream().mapToDouble(LineItem::subtotal).sum();
    }

    protected static String trim(double v) { return String.format(java.util.Locale.ROOT, "%.1f", v); }
}