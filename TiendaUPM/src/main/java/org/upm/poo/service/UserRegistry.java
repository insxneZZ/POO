package org.upm.poo.service;

import org.upm.poo.domain.user.Cashier;
import org.upm.poo.domain.user.Client;
import java.util.*;

public class UserRegistry {
    private static final UserRegistry INSTANCE = new UserRegistry();

    private final Map<String, Client> clients = new LinkedHashMap<>();
    private final Map<String, Cashier> cashiers = new LinkedHashMap<>();

    private UserRegistry() {}

    public static UserRegistry getInstance() {
        return INSTANCE;
    }

    public Client addClient(Client c) {
        if (clients.containsKey(c.getId())) throw new IllegalArgumentException("Client already exists");
        clients.put(c.getId(), c);
        return c;
    }

    public Client getClient(String id) {
        Client c = clients.get(id);
        if (c == null) throw new NoSuchElementException("Client not found: " + id);
        return c;
    }

    public void removeClient(String id) {
        if (clients.remove(id) == null) throw new NoSuchElementException("Client not found: " + id);
    }

    public Collection<Client> listClients() {
        return clients.values();
    }

    public Cashier addCashier(Cashier c) {
        if (cashiers.containsKey(c.getId())) throw new IllegalArgumentException("Cashier already exists");
        cashiers.put(c.getId(), c);
        return c;
    }

    public void removeCashier(String id) {
        if (cashiers.remove(id) == null) throw new NoSuchElementException("Cashier not found: " + id);
    }

    public Collection<Cashier> listCashiers() {
        return cashiers.values();
    }
}