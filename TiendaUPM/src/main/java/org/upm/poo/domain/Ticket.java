package org.upm.poo.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class Ticket {
    private String id;
    private final String cashierId;
    private final String clientId;
    private TicketState state;
    private transient TicketPolicy policy;

    private final List<LineItem> items = new ArrayList<>();

    private static final DateTimeFormatter ID_FMT = DateTimeFormatter.ofPattern("yy-MM-dd-HH:mm");

    public Ticket(String id, String cashierId, String clientId) {
        if (cashierId == null || cashierId.isBlank()) throw new IllegalArgumentException("Cashier ID required");
        if (clientId == null || clientId.isBlank()) throw new IllegalArgumentException("Client ID required");

        this.cashierId = cashierId;
        this.clientId = clientId;
        this.state = TicketState.EMPTY;
        this.id = (id == null || id.isBlank()) ? generateInitialId() : id;

        this.policy = new StandardTicketPolicy();
    }

    public void setPolicy(TicketPolicy policy) {
        this.policy = policy;
    }

    public TicketPolicy getPolicy() {
        return policy;
    }

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

        policy.checkAddition(p);

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

    public void add(Product p, int q) {
        add(p, q, List.of());
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

    private void updateState() {
        if (state == TicketState.CLOSED) return;
        this.state = items.isEmpty() ? TicketState.EMPTY : TicketState.ACTIVE;
    }

    public double totalPrice() {
        return items.stream().mapToDouble(LineItem::subtotal).sum();
    }

    public double totalDiscount() {
        return policy.calculateTotalDiscount(this);
    }

    public double finalPrice() {
        return policy.calculateFinalPrice(this);
    }

    public double unitDiscountFor(Category c, double unitPrice) {
        if (policy instanceof StandardTicketPolicy stp) {
            int catUnits = items.stream()
                    .filter(li -> li.getProduct() instanceof ItemProduct ip && ip.getCategory() == c)
                    .mapToInt(LineItem::getQuantity).sum();
            return new CategoryQuantityDiscountPolicy().unitDiscount(c, unitPrice, catUnits);
        }
        return 0.0;
    }
}