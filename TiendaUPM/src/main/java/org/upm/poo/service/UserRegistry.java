package org.upm.poo.service;

import org.upm.poo.domain.user.Cashier;
import org.upm.poo.domain.user.Client;
import org.upm.poo.domain.user.Company;
import org.upm.poo.domain.user.Customer;
import java.util.*;

public final class UserRegistry {
    private final Map<String, Customer> customers = new LinkedHashMap<>();
    private final Map<String, Cashier> cashiers = new LinkedHashMap<>();

    public Client addClient(Client c) {
        addCustomer(c);
        return c;
    }

    public Company addCompany(Company c) {
        addCustomer(c);
        return c;
    }

    private void addCustomer(Customer c) {
        if (customers.containsKey(c.getId())) {
            throw new IllegalArgumentException("Customer ID (DNI/NIF) already exists: " + c.getId());
        }
        customers.put(c.getId(), c);
    }

    public Client getClient(String dni) {
        Customer c = getCustomer(dni);
        if (!(c instanceof Client)) throw new NoSuchElementException("ID " + dni + " is not a Client");
        return (Client) c;
    }

    public Company getCompany(String nif) {
        Customer c = getCustomer(nif);
        if (!(c instanceof Company)) throw new NoSuchElementException("ID " + nif + " is not a Company");
        return (Company) c;
    }

    public Customer getCustomer(String id) {
        Customer c = customers.get(id);
        if (c == null) throw new NoSuchElementException("Customer not found: " + id);
        return c;
    }

    public Customer removeCustomer(String id) {
        Customer c = customers.remove(id);
        if (c == null) throw new NoSuchElementException("Customer not found: " + id);
        return c;
    }

    // Métodos de compatibilidad para CommandLoop existente
    public Client removeClient(String dni) {
        Customer c = customers.get(dni);
        if (c == null || !(c instanceof Client)) throw new NoSuchElementException("Client not found: " + dni);
        customers.remove(dni);
        return (Client) c;
    }

    public Collection<Client> listClients() {
        List<Client> list = new ArrayList<>();
        for (Customer c : customers.values()) {
            if (c instanceof Client cl) list.add(cl);
        }
        return list;
    }

    public Collection<Company> listCompanies() {
        List<Company> list = new ArrayList<>();
        for (Customer c : customers.values()) {
            if (c instanceof Company co) list.add(co);
        }
        return list;
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
        return Collections.unmodifiableCollection(cashiers.values());
    }
}