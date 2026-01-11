package org.upm.poo.service;

import org.upm.poo.domain.user.*;

import java.util.*;

public final class UserRegistry {
    private final Map<String, Client> clients = new LinkedHashMap<>();
    private final Map<String, Cashier> cashiers = new LinkedHashMap<>();

    public Client addClient(Client c) {
        if (clients.containsKey(c.getId())) {
            throw new IllegalArgumentException("Client ID already exists: " + c.getId());
        }
        clients.put(c.getId(), c);
        return c;
    }

    public Client getClient(String id) {
        Client c = clients.get(id);
        if (c == null) throw new NoSuchElementException("Client not found: " + id);
        return c;
    }

    public Client removeClient(String id) {
        Client c = clients.remove(id);
        if (c == null) throw new NoSuchElementException("Client not found: " + id);
        return c;
    }

    public Collection<Client> listClients() {
        return clients.values();
    }

    public Cashier addCashier(Cashier c) {
        if (cashiers.containsKey(c.getId())) {
            throw new IllegalArgumentException("Cashier ID already exists: " + c.getId());
        }
        cashiers.put(c.getId(), c);
        return c;
    }

    public Cashier getCashier(String id) {
        Cashier c = cashiers.get(id);
        if (c == null) throw new NoSuchElementException("Cashier not found: " + id);
        return c;
    }

    public Cashier removeCashier(String id) {
        Cashier c = cashiers.remove(id);
        if (c == null) throw new NoSuchElementException("Cashier not found: " + id);
        return c;
    }

    public Collection<Cashier> listCashiers() {
        return cashiers.values();
    }
}