package org.upm.poo.io;

import org.upm.poo.domain.Product;
import org.upm.poo.domain.Ticket;
import org.upm.poo.domain.user.Cashier;
import org.upm.poo.domain.user.Customer;

import java.io.*;
import java.util.*;

public class AppPersister {
    private static final String DATA_FILE = "tienda_upm_db.dat";

    private static class WorldState implements Serializable {
        List<Product> products;
        List<Customer> customers;
        List<Cashier> cashiers;
        List<Ticket<?>> tickets;

        WorldState(Collection<Product> p, Collection<Customer> c, Collection<Cashier> ca, Collection<Ticket<?>> t) {
            this.products = new ArrayList<>(p);
            this.customers = new ArrayList<>(c);
            this.cashiers = new ArrayList<>(ca);
            this.tickets = new ArrayList<>(t);
        }
    }

    public static void save(Collection<Product> products,
                            Collection<Customer> customers,
                            Collection<Cashier> cashiers,
                            Collection<Ticket<?>> tickets) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            WorldState state = new WorldState(products, customers, cashiers, tickets);
            oos.writeObject(state);
        } catch (IOException e) {
            System.err.println("Error saving persistence: " + e.getMessage());
        }
    }

    public static WorldState load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (WorldState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading persistence: " + e.getMessage());
            return null;
        }
    }

    // Métodos auxiliares para extraer datos del WorldState
    public static List<Product> getProducts(Object state) { return state == null ? List.of() : ((WorldState)state).products; }
    public static List<Customer> getCustomers(Object state) { return state == null ? List.of() : ((WorldState)state).customers; }
    public static List<Cashier> getCashiers(Object state) { return state == null ? List.of() : ((WorldState)state).cashiers; }
    public static List<Ticket<?>> getTickets(Object state) { return state == null ? List.of() : ((WorldState)state).tickets; }
}