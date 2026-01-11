package org.upm.poo.service;

import org.upm.poo.domain.*;
import org.upm.poo.domain.user.Client;
import java.util.*;

public final class TicketService {
    private final Map<String, Ticket> tickets = new LinkedHashMap<>();

    private final UserRegistry userRegistry;

    public TicketService(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public Ticket createTicket(String id, String cashierId, String clientId) {
        if (tickets.containsKey(id)) {
            throw new IllegalArgumentException("Ticket ID already exists: " + id);
        }

        Client client = userRegistry.getClient(clientId);

        Ticket t = new Ticket(id, cashierId, clientId);

        if (client.isCompany()) {
            t.setPolicy(new EnterpriseTicketPolicy());
        } else {
            t.setPolicy(new StandardTicketPolicy());
        }

        tickets.put(t.getId(), t);
        return t;
    }

    public Ticket getTicket(String id) {
        Ticket t = tickets.get(id);
        if (t == null) throw new NoSuchElementException("Ticket not found: " + id);
        return t;
    }

    public Collection<Ticket> findAll() {
        return tickets.values();
    }

    public void verifyOwner(Ticket t, String cashierId) {
        if (!t.getCashierId().equals(cashierId)) {
            throw new SecurityException("Operation denied: Ticket " + t.getId() + " belongs to cashier " + t.getCashierId());
        }
    }

    public void removeTicketsByCashier(String cashierId) {
        tickets.values().removeIf(t -> t.getCashierId().equals(cashierId));
    }

    public void loadTicketRaw(Ticket t) {
        tickets.put(t.getId(), t);
    }
}