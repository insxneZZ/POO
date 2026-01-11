package org.upm.poo.domain.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class Customer implements Serializable {
    private final String id;
    private String name;
    private String email;
    private final String creatorCashierId;
    private final List<String> ticketIds = new ArrayList<>();

    protected Customer(String id, String name, String email, String creatorCashierId) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email required");
        if (creatorCashierId == null || creatorCashierId.isBlank()) throw new IllegalArgumentException("Creator Cashier ID required");

        this.id = id;
        this.name = name;
        this.email = email;
        this.creatorCashierId = creatorCashierId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCreatorCashierId() { return creatorCashierId; }

    public List<String> getTicketIds() { return Collections.unmodifiableList(ticketIds); }

    public void addTicketId(String ticketId) {
        if(ticketId != null && !ticketIds.contains(ticketId)) {
            ticketIds.add(ticketId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{identifier='" + getId() + "', name='" + getName() + "', email='" + getEmail() + "', cash=" + getCreatorCashierId() + "}";
    }
}