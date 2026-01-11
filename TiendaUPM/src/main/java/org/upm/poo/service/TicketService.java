package org.upm.poo.service;

import org.upm.poo.domain.*;
import org.upm.poo.domain.StandardTicket;
import org.upm.poo.domain.user.Client;
import org.upm.poo.domain.user.Company;
import org.upm.poo.domain.user.Customer;

import java.util.*;

public final class TicketService {
    private final Map<String, Ticket<?>> tickets = new LinkedHashMap<>();
    private final UserRegistry userRegistry;

    public TicketService(UserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    public void registerTicket(Ticket<?> t) {
        tickets.put(t.getId(), t);
    }

    public Ticket<?> createTicket(String id, String cashierId, String customerId, String typeFlag) {
        if (id != null && tickets.containsKey(id)) {
            throw new IllegalArgumentException("Ticket ID already exists: " + id);
        }

        Customer customer = userRegistry.getCustomer(customerId);
        Ticket<?> t;

        if (customer instanceof Client client) {
            t = new StandardTicket(id, cashierId, client);
        } else if (customer instanceof Company company) {
            EnterpriseTicket.Mode mode;
            if ("-s".equals(typeFlag)) {
                mode = EnterpriseTicket.Mode.SERVICE_ONLY;
            } else if ("-c".equals(typeFlag)) {
                mode = EnterpriseTicket.Mode.COMBINED;
            } else {
                throw new IllegalArgumentException("Companies require flag -s (Service) or -c (Combined).");
            }
            t = new EnterpriseTicket(id, cashierId, company, mode);
        } else {
            throw new IllegalStateException("Unknown customer type");
        }

        tickets.put(t.getId(), t);
        return t;
    }

    public Ticket<?> getTicket(String id) {
        Ticket<?> t = tickets.get(id);
        if (t == null) throw new NoSuchElementException("Ticket not found: " + id);
        return t;
    }

    public Collection<Ticket<?>> findAll() {
        return tickets.values();
    }

    public void verifyOwner(Ticket<?> t, String cashierId) {
        if (!t.getCashierId().equals(cashierId)) {
            throw new SecurityException("Operation denied: Ticket " + t.getId() + " belongs to cashier " + t.getCashierId());
        }
    }

    public void removeTicketsByCashier(String cashierId) {
        tickets.values().removeIf(t -> t.getCashierId().equals(cashierId));
    }
}