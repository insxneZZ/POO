package org.upm.poo.cli;

import org.upm.poo.domain.*;
import org.upm.poo.domain.user.Cashier;
import org.upm.poo.domain.user.Client;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.TicketService;
import org.upm.poo.service.UserRegistry;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public final class CommandLoop {
    private final Catalog catalog;
    private final TicketService tickets;
    private final UserRegistry userRegistry;

    public CommandLoop(Catalog catalog, TicketService tickets, UserRegistry userRegistry) {
        this.catalog = catalog;
        this.tickets = tickets;
        this.userRegistry = userRegistry;
    }

    public void run() throws Exception {
        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("tUPM> ");
                String line = br.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isBlank()) continue;

                try {
                    if (line.equals("help")) { printHelp(); continue; }
                    if (line.startsWith("echo ")) { System.out.println(line.substring(5)); continue; }
                    if (line.equals("exit")) { System.out.println("Closing application."); System.out.println("Goodbye!"); break; }

                    List<String> a = CommandParser.splitArgs(line);
                    switch (a.get(0)) {
                        case "prod"   -> handleProd(a);
                        case "ticket" -> handleTicket(a);
                        case "client" -> handleClient(a);
                        case "cash"   -> handleCash(a);
                        default       -> System.out.println("Unknown command. Type 'help'.");
                    }
                } catch (IllegalArgumentException | NoSuchElementException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                }
            }
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add <id> \"<name>\" <category> <price>");
        System.out.println("  prod addFood [<id>] \"<name>\" <price> <yyyy-MM-dd> <maxPeople>");
        System.out.println("  prod addMeeting [<id>] \"<name>\" <price> <yyyy-MM-dd> <maxPeople>");
        System.out.println("  prod list");
        System.out.println("  prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println("  prod remove <id>");
        System.out.println("  client add \"<name>\" <DNI> <email> <cashId>");
        System.out.println("  client remove <DNI>");
        System.out.println("  client list");
        System.out.println("  cash add [<id>] \"<name>\" <email>");
        System.out.println("  cash remove <id>");
        System.out.println("  cash list");
        System.out.println("  cash tickets <id>");
        System.out.println("  ticket new");
        System.out.println("  ticket add <prodId> <quantity>");
        System.out.println("  ticket remove <prodId>");
        System.out.println("  ticket print");
        System.out.println("  echo \"<texto>\"");
        System.out.println("  help");
        System.out.println("  exit");
        System.out.println();
    }

    private void handleClient(List<String> a) {
        if (a.size() < 2) { System.out.println("Usage: client ..."); return; }
        switch (a.get(1)) {
            case "add" -> {
                if (a.size() < 6) {
                    System.out.println("Usage: client add \"<name>\" <DNI> <email> <cashId>");
                    return;
                }
                String name = a.get(2);
                String dni = a.get(3);
                String email = a.get(4);
                String cashId = a.get(5);

                Client c = userRegistry.addClient(new Client(dni, name, email, cashId));
                System.out.println(c);
                System.out.println("client add: ok");
            }
            case "remove" -> {
                if (a.size() < 3) { System.out.println("Usage: client remove <DNI>"); return; }
                String dni = a.get(2);
                Client c = userRegistry.removeClient(dni);
                System.out.println(c);
                System.out.println("client remove: ok");
            }
            case "list" -> {
                System.out.println("Clients:");
                userRegistry.listClients().stream()
                        .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                        .forEach(c -> System.out.println("  " + c));
                System.out.println("client list: ok");
            }
            default -> System.out.println("Unknown client command");
        }
    }

    private void handleCash(List<String> a) {
        if (a.size() < 2) { System.out.println("Usage: cash ..."); return; }
        switch (a.get(1)) {
            case "add" -> {
                if (a.size() < 4) {
                    System.out.println("Usage: cash add [<id>] \"<name>\" <email>");
                    return;
                }
                String id = null;
                String name;
                String email;

                if (a.size() == 5) {
                    id = a.get(2);
                    name = a.get(3);
                    email = a.get(4);
                } else {
                    name = a.get(2);
                    email = a.get(3);
                }

                Cashier c = userRegistry.addCashier(new Cashier(id, name, email));
                System.out.println(c);
                System.out.println("cash add: ok");
            }
            case "remove" -> {
                if (a.size() < 3) { System.out.println("Usage: cash remove <id>"); return; }
                String id = a.get(2);
                Cashier c = userRegistry.removeCashier(id);
                System.out.println(c);
                System.out.println("cash remove: ok");
            }
            case "list" -> {
                System.out.println("Cashiers:");
                userRegistry.listCashiers().stream()
                        .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                        .forEach(c -> System.out.println("  " + c));
                System.out.println("cash list: ok");
            }
            case "tickets" -> {
                if (a.size() < 3) { System.out.println("Usage: cash tickets <id>"); return; }
                System.out.println("TODO: List tickets for cashier " + a.get(2) + " (Requires Ticket update)");
            }
            default -> System.out.println("Unknown cash command");
        }
    }

    private void handleProd(List<String> a) {
        if (a.size() < 2) { System.out.println("Usage: prod ..."); return; }
        switch (a.get(1)) {
            case "add" -> {
                if (a.size() < 6) { System.out.println("Usage: prod add <id> \"<name>\" <category> <price>"); return; }
                String id = a.get(2);
                String name = a.get(3);
                Category cat = Category.valueOf(a.get(4));
                double price = Double.parseDouble(a.get(5));
                Product p = catalog.add(new StandardProduct(id, name, cat, price));
                System.out.println(p);
                System.out.println("prod add: ok");
            }
            case "addFood" -> {
                if (a.size() < 6) { System.out.println("Usage: prod addFood [<id>] \"<name>\" <price> <yyyy-MM-dd> <maxPeople>"); return; }
                String id;
                String name;
                double price;
                LocalDate expiration;
                int maxPeople;
                if (a.size() == 7) {
                    id = a.get(2);
                    name = a.get(3);
                    price = Double.parseDouble(a.get(4));
                    expiration = LocalDate.parse(a.get(5));
                    maxPeople = Integer.parseInt(a.get(6));
                } else {
                    id = "P-" + UUID.randomUUID().toString().substring(0, 5);
                    name = a.get(2);
                    price = Double.parseDouble(a.get(3));
                    expiration = LocalDate.parse(a.get(4));
                    maxPeople = Integer.parseInt(a.get(5));
                }
                try {
                    Product p = catalog.add(new Food(id, name, price, expiration, maxPeople));
                    System.out.println(p);
                    System.out.println("prod addFood: ok");
                } catch (Exception e) { System.out.println("Error creating Food: " + e.getMessage()); }
            }
            case "addMeeting" -> {
                if (a.size() < 6) { System.out.println("Usage: prod addMeeting [<id>] \"<name>\" <price> <yyyy-MM-dd> <maxPeople>"); return; }
                String id;
                String name;
                double price;
                LocalDate expiration;
                int maxPeople;
                if (a.size() == 7) {
                    id = a.get(2);
                    name = a.get(3);
                    price = Double.parseDouble(a.get(4));
                    expiration = LocalDate.parse(a.get(5));
                    maxPeople = Integer.parseInt(a.get(6));
                } else {
                    id = "P-" + UUID.randomUUID().toString().substring(0, 5);
                    name = a.get(2);
                    price = Double.parseDouble(a.get(3));
                    expiration = LocalDate.parse(a.get(4));
                    maxPeople = Integer.parseInt(a.get(5));
                }
                try {
                    Product p = catalog.add(new Meeting(id, name, price, expiration, maxPeople));
                    System.out.println(p);
                    System.out.println("prod addMeeting: ok");
                } catch (Exception e) { System.out.println("Error creating Meeting: " + e.getMessage()); }
            }
            case "list" -> {
                System.out.println("Catalog:");
                catalog.list().forEach(p -> System.out.println("  " + p));
                System.out.println("prod list: ok");
            }
            case "update" -> {
                if (a.size() < 5) { System.out.println("Usage: prod update <id> NAME|CATEGORY|PRICE <value>"); return; }
                String id = a.get(2);
                String field = a.get(3);
                String value = a.get(4);
                Product p = catalog.get(id);
                switch (field) {
                    case "NAME"     -> p.setName(value);
                    case "PRICE"    -> p.setPrice(Double.parseDouble(value));
                    case "CATEGORY" -> {
                        if (p instanceof ItemProduct ip) ip.setCategory(Category.valueOf(value));
                        else { System.out.println("Error: Product does not support categories"); return; }
                    }
                    default -> throw new IllegalArgumentException("Unknown field: " + field);
                }
                System.out.println(p);
                System.out.println("prod update: ok");
            }
            case "remove" -> {
                if (a.size() < 3) { System.out.println("Usage: prod remove <id>"); return; }
                String id = a.get(2);
                Product removed = catalog.remove(id);
                System.out.println(removed);
                System.out.println("prod remove: ok");
            }
            default -> System.out.println("Unknown prod command");
        }
    }

    private void handleTicket(List<String> a) {
        if (a.size() < 2) { System.out.println("Usage: ticket ..."); return; }
        switch (a.get(1)) {
            case "new" -> {
                if (a.size() < 4) { System.out.println("Usage: ticket new [<id>] <cashId> <userId>"); return; }

                String id = null;
                String cashId;
                String userId;

                if (a.size() == 5) {
                    id = a.get(2);
                    cashId = a.get(3);
                    userId = a.get(4);
                } else {
                    cashId = a.get(2);
                    userId = a.get(3);
                }

                Ticket t = tickets.createTicket(id, cashId, userId);
                System.out.println(t);
                System.out.println("ticket new: ok");
            }
            case "add" -> {
                if (a.size() < 6) { System.out.println("Usage: ticket add <ticketId> <cashId> <prodId> <amount>"); return; }

                String ticketId = a.get(2);
                String cashId = a.get(3);
                String prodId = a.get(4);
                int amount = Integer.parseInt(a.get(5));

                Ticket t = tickets.getTicket(ticketId);
                tickets.verifyOwner(t, cashId);

                Product p = catalog.get(prodId);
                t.add(p, amount);

                double unitDisc = 0.0;
                if (p instanceof ItemProduct ip) unitDisc = t.unitDiscountFor(ip.getCategory(), p.getPrice());

                for (int i = 0; i < amount; i++) {
                    if (unitDisc > 0) System.out.println(p + " **discount -" + trim(unitDisc));
                    else System.out.println(p);
                }
                System.out.println("Total price: " + trim(t.totalPrice()));
                System.out.println("Total discount: " + trim(t.totalDiscount()));
                System.out.println("Final Price: " + trim(t.finalPrice()));
                System.out.println("ticket add: ok");
            }
            case "remove" -> {
                if (a.size() < 5) { System.out.println("Usage: ticket remove <ticketId> <cashId> <prodId>"); return; }

                String ticketId = a.get(2);
                String cashId = a.get(3);
                String prodId = a.get(4);

                Ticket t = tickets.getTicket(ticketId);
                tickets.verifyOwner(t, cashId);

                t.remove(prodId);
                System.out.println("ticket remove: ok");
            }
            case "print" -> {
                if (a.size() < 4) { System.out.println("Usage: ticket print <ticketId> <cashId>"); return; }

                String ticketId = a.get(2);
                String cashId = a.get(3);

                Ticket t = tickets.getTicket(ticketId);
                tickets.verifyOwner(t, cashId);

                t.getItems().stream()
                        .sorted((li1, li2) -> li1.getProduct().getName().compareToIgnoreCase(li2.getProduct().getName()))
                        .forEach(li -> {
                            double unitDisc = 0.0;
                            if (li.getProduct() instanceof ItemProduct ip) {
                                unitDisc = t.unitDiscountFor(ip.getCategory(), ip.getPrice());
                            }
                            for (int i = 0; i < li.getQuantity(); i++) {
                                if (unitDisc > 0) System.out.println(li.getProduct() + " **discount -" + trim(unitDisc));
                                else System.out.println(li.getProduct());
                            }
                        });

                System.out.println("Total price: " + trim(t.totalPrice()));
                System.out.println("Total discount: " + trim(t.totalDiscount()));
                System.out.println("Final Price: " + trim(t.finalPrice()));

                t.close();
                System.out.println("ticket print: ok");
            }
            case "list" -> {
                System.out.println("Tickets:");
                tickets.findAll().stream()
                        .sorted((t1, t2) -> t1.getCashierId().compareTo(t2.getCashierId()))
                        .forEach(t -> System.out.println("  " + t.getId() + " [" + t.getState() + "] Cashier:" + t.getCashierId()));
                System.out.println("ticket list: ok");
            }
            default -> System.out.println("Unknown ticket command");
        }
    }

    private static String trim(double v) {
        return String.format(java.util.Locale.ROOT, "%.1f", v);
    }
}