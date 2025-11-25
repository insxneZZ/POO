package org.upm.poo.cli;

import org.upm.poo.domain.*;
import org.upm.poo.domain.user.Cashier;
import org.upm.poo.domain.user.Client;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.TicketService;
import org.upm.poo.service.UserRegistry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
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

    public void run(String inputFilePath) throws Exception {
        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        Reader inputReader;
        boolean isFileMode = false;

        if (inputFilePath != null) {
            inputReader = new FileReader(inputFilePath);
            isFileMode = true;
        } else {
            inputReader = new InputStreamReader(System.in);
        }

        try (BufferedReader br = new BufferedReader(inputReader)) {
            while (true) {
                if (!isFileMode) System.out.print("tUPM> ");

                String line = br.readLine();
                if (line == null) break;

                line = line.trim();
                if (line.isBlank()) continue;

                if (isFileMode) {
                    System.out.println("tUPM> " + line);
                }

                try {
                    if (line.equals("help")) { printHelp(); continue; }
                    if (line.startsWith("echo ")) { System.out.println(line.substring(5)); continue; }
                    if (line.equals("exit")) { System.out.println("Closing application."); System.out.println("Goodbye!"); break; }

                    List<String> a = CommandParser.splitArgs(line);
                    if (a.isEmpty()) continue;

                    switch (a.get(0)) {
                        case "prod"   -> handleProd(a);
                        case "ticket" -> handleTicket(a);
                        case "client" -> handleClient(a);
                        case "cash"   -> handleCash(a);
                        default       -> System.out.println("Unknown command. Type 'help'.");
                    }
                } catch (IllegalArgumentException | NoSuchElementException | IllegalStateException | SecurityException ex) {
                    System.out.println(ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
                }
            }
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add [<id>] \"<name>\" <category> <price> [<maxPers>]");
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
        System.out.println("  ticket new [<id>] <cashId> <userId>");
        System.out.println("  ticket add <ticketId> <cashId> <prodId> <qty> [--p <text> ...]");
        System.out.println("  ticket remove <ticketId> <cashId> <prodId>");
        System.out.println("  ticket print <ticketId> <cashId>");
        System.out.println("  echo \"<texto>\"");
        System.out.println("  help");
        System.out.println("  exit");
    }

    // --- MANEJO DE PRODUCTOS ---
    private void handleProd(List<String> a) {
        if (a.size() < 2) { System.out.println("Usage: prod ..."); return; }
        switch (a.get(1)) {
            case "add" -> {
                if (a.size() < 5) { System.out.println("Usage: prod add [<id>] \"<name>\" <category> <price> [<maxPers>]"); return; }

                String id = null;
                String name;
                Category cat;
                double price;
                Integer maxPers = null;

                boolean isCategoryAt3 = isCategory(a.get(3));
                boolean isCategoryAt4 = (a.size() >= 5) && isCategory(a.get(4));

                if (isCategoryAt4) {
                    id = a.get(2);
                    name = a.get(3);
                    cat = Category.valueOf(a.get(4));
                    price = Double.parseDouble(a.get(5));
                    if (a.size() > 6) maxPers = Integer.parseInt(a.get(6));
                } else if (isCategoryAt3) {
                    id = "P-" + UUID.randomUUID().toString().substring(0, 5);
                    name = a.get(2);
                    cat = Category.valueOf(a.get(3));
                    price = Double.parseDouble(a.get(4));
                    if (a.size() > 5) maxPers = Integer.parseInt(a.get(5));
                } else {
                    System.out.println("Error: Invalid arguments or unknown category."); return;
                }

                Product p = (maxPers != null) ?
                        new CustomizableProduct(id, name, cat, price, maxPers) :
                        new StandardProduct(id, name, cat, price);

                catalog.add(p);
                System.out.println(p);
                System.out.println("prod add: ok");
            }
            case "addFood" -> {
                if (a.size() < 6) { System.out.println("Usage: prod addFood ..."); return; }
                String id, name;
                double price;
                LocalDate exp;
                int maxP;
                if (a.size() == 7) {
                    id = a.get(2); name = a.get(3); price = Double.parseDouble(a.get(4));
                    exp = LocalDate.parse(a.get(5)); maxP = Integer.parseInt(a.get(6));
                } else {
                    id = "F-" + UUID.randomUUID().toString().substring(0, 5);
                    name = a.get(2); price = Double.parseDouble(a.get(3));
                    exp = LocalDate.parse(a.get(4)); maxP = Integer.parseInt(a.get(5));
                }
                try {
                    Product p = catalog.add(new Food(id, name, price, exp, maxP));
                    System.out.println(p);
                    System.out.println("prod addFood: ok");
                } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
            }
            case "addMeeting" -> {
                if (a.size() < 6) { System.out.println("Usage: prod addMeeting ..."); return; }
                String id, name;
                double price;
                LocalDate exp;
                int maxP;
                if (a.size() == 7) {
                    id = a.get(2); name = a.get(3); price = Double.parseDouble(a.get(4));
                    exp = LocalDate.parse(a.get(5)); maxP = Integer.parseInt(a.get(6));
                } else {
                    id = "M-" + UUID.randomUUID().toString().substring(0, 5);
                    name = a.get(2); price = Double.parseDouble(a.get(3));
                    exp = LocalDate.parse(a.get(4)); maxP = Integer.parseInt(a.get(5));
                }
                try {
                    Product p = catalog.add(new Meeting(id, name, price, exp, maxP));
                    System.out.println(p);
                    System.out.println("prod addMeeting: ok");
                } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
            }
            case "list" -> {
                System.out.println("Catalog:");
                catalog.list().forEach(p -> System.out.println("  " + p));
                System.out.println("prod list: ok");
            }
            case "update" -> {
                if (a.size() < 5) { System.out.println("Usage: prod update <id> ..."); return; }
                String id = a.get(2); String field = a.get(3); String value = a.get(4);
                Product p = catalog.get(id);
                switch (field) {
                    case "NAME" -> p.setName(value);
                    case "PRICE" -> p.setPrice(Double.parseDouble(value));
                    case "CATEGORY" -> {
                        if (p instanceof ItemProduct ip) ip.setCategory(Category.valueOf(value));
                        else System.out.println("Error: Not categorizable");
                    }
                    default -> System.out.println("Unknown field");
                }
                System.out.println(p);
                System.out.println("prod update: ok");
            }
            case "remove" -> {
                if (a.size() < 3) return;
                System.out.println(catalog.remove(a.get(2)));
                System.out.println("prod remove: ok");
            }
            default -> System.out.println("Unknown prod command");
        }
    }

    private boolean isCategory(String s) {
        try { Category.valueOf(s); return true; } catch (Exception e) { return false; }
    }

    // --- MANEJO DE TICKETS ---
    private void handleTicket(List<String> a) {
        if (a.size() < 2) { System.out.println("Usage: ticket ..."); return; }
        switch (a.get(1)) {
            case "new" -> {
                if (a.size() < 4) { System.out.println("Usage: ticket new [<id>] <cashId> <userId>"); return; }
                String id = null, cashId, userId;
                if (a.size() == 5) { id = a.get(2); cashId = a.get(3); userId = a.get(4); }
                else { cashId = a.get(2); userId = a.get(3); }

                Ticket t = tickets.createTicket(id, cashId, userId);
                System.out.println(t.getId() + " created (State: " + t.getState() + ")");
                System.out.println("ticket new: ok");
            }
            case "add" -> {
                if (a.size() < 6) { System.out.println("Usage: ticket add <ticketId> <cashId> <prodId> <qty> ..."); return; }
                String tId = a.get(2);
                String cashId = a.get(3);
                String pId = a.get(4);
                int qty = Integer.parseInt(a.get(5));

                List<String> customs = new ArrayList<>();
                for (int i = 6; i < a.size(); i++) {
                    String arg = a.get(i);
                    if (arg.startsWith("--p")) {
                        if (arg.equals("--p")) {
                            if (i + 1 < a.size()) { customs.add(a.get(i + 1)); i++; }
                        } else {
                            customs.add(arg.substring(3));
                        }
                    }
                }

                Ticket t = tickets.getTicket(tId);
                tickets.verifyOwner(t, cashId);
                Product p = catalog.get(pId);

                t.add(p, qty, customs);

                double unitDisc = 0.0;
                if (p instanceof ItemProduct ip) unitDisc = t.unitDiscountFor(ip.getCategory(), p.getPrice());

                double basePrice = p.getPrice();
                double extraCharge = (!customs.isEmpty()) ? basePrice * 0.10 * customs.size() : 0.0;
                double finalUnit = basePrice + extraCharge;

                for (int i = 0; i < qty; i++) {
                    String info = p.toString();
                    if (!customs.isEmpty()) info += " " + customs;

                    if (unitDisc > 0) System.out.println(info + " (Unit: " + trim(finalUnit) + ") **discount -" + trim(unitDisc));
                    else System.out.println(info + " (Unit: " + trim(finalUnit) + ")");
                }

                System.out.println("Total price: " + trim(t.totalPrice()));
                System.out.println("Total discount: " + trim(t.totalDiscount()));
                System.out.println("Final Price: " + trim(t.finalPrice()));
                System.out.println("ticket add: ok");
            }
            case "remove" -> {
                if (a.size() < 5) return;
                Ticket t = tickets.getTicket(a.get(2));
                tickets.verifyOwner(t, a.get(3));
                t.remove(a.get(4));
                System.out.println("ticket remove: ok");
            }
            case "print" -> {
                if (a.size() < 4) return;
                Ticket t = tickets.getTicket(a.get(2));
                tickets.verifyOwner(t, a.get(3));

                t.getItems().stream()
                        .sorted((l1, l2) -> l1.getProduct().getName().compareToIgnoreCase(l2.getProduct().getName()))
                        .forEach(li -> {
                            Product p = li.getProduct();
                            double uDisc = (p instanceof ItemProduct ip) ? t.unitDiscountFor(ip.getCategory(), ip.getPrice()) : 0.0;

                            double base = p.getPrice();
                            double extra = (!li.getCustomizations().isEmpty()) ? base * 0.10 * li.getCustomizations().size() : 0.0;
                            double finalU = base + extra;

                            for(int i=0; i<li.getQuantity(); i++) {
                                String info = p.toString();
                                if(!li.getCustomizations().isEmpty()) info += " " + li.getCustomizations();
                                if (uDisc > 0) System.out.println(info + " (Unit: " + trim(finalU) + ") **discount -" + trim(uDisc));
                                else System.out.println(info + " (Unit: " + trim(finalU) + ")");
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
                        .forEach(t -> System.out.println("  ID:" + t.getId() + " State:" + t.getState() + " Cashier:" + t.getCashierId()));
                System.out.println("ticket list: ok");
            }
            default -> System.out.println("Unknown ticket command");
        }
    }

    // --- MANEJO DE CLIENTES/CAJEROS ---
    private void handleClient(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "add" -> {
                if (a.size() < 6) { System.out.println("Usage: client add ..."); return; }
                Client c = userRegistry.addClient(new Client(a.get(3), a.get(2), a.get(4), a.get(5)));
                System.out.println(c); System.out.println("client add: ok");
            }
            case "remove" -> {
                if (a.size() < 3) return;
                System.out.println(userRegistry.removeClient(a.get(2))); System.out.println("client remove: ok");
            }
            case "list" -> {
                System.out.println("Clients:");
                userRegistry.listClients().stream()
                        .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                        .forEach(c -> System.out.println("  " + c));
                System.out.println("client list: ok");
            }
        }
    }

    private void handleCash(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "add" -> {
                if (a.size() < 4) { System.out.println("Usage: cash add ..."); return; }
                String id = null, name, email;
                if (a.size() == 5) { id = a.get(2); name = a.get(3); email = a.get(4); }
                else { name = a.get(2); email = a.get(3); }
                Cashier c = userRegistry.addCashier(new Cashier(id, name, email));
                System.out.println(c); System.out.println("cash add: ok");
            }
            case "remove" -> {
                if (a.size() < 3) return;
                System.out.println(userRegistry.removeCashier(a.get(2))); System.out.println("cash remove: ok");
            }
            case "list" -> {
                System.out.println("Cashiers:");
                userRegistry.listCashiers().stream()
                        .sorted((c1,c2)->c1.getName().compareToIgnoreCase(c2.getName()))
                        .forEach(c -> System.out.println("  " + c));
                System.out.println("cash list: ok");
            }
            case "tickets" -> {
                if (a.size() < 3) return;
                String cashId = a.get(2);
                System.out.println("Tickets of " + cashId + ":");
                tickets.findAll().stream()
                        .filter(t -> t.getCashierId().equals(cashId))
                        .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
                        .forEach(t -> System.out.println("  " + t.getId() + " [" + t.getState() + "]"));
                System.out.println("cash tickets: ok");
            }
        }
    }

    private static String trim(double v) { return String.format(java.util.Locale.ROOT, "%.1f", v); }
}