package org.upm.poo.cli;

import org.upm.poo.domain.*;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.TicketService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public final class CommandLoop {
    private final Catalog catalog;
    private final TicketService tickets;

    public CommandLoop(Catalog catalog, TicketService tickets) {
        this.catalog = catalog;
        this.tickets = tickets;
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
        System.out.println("  ticket new");
        System.out.println("  ticket add <prodId> <quantity>");
        System.out.println("  ticket remove <prodId>");
        System.out.println("  ticket print");
        System.out.println("  echo \"<texto>\"");
        System.out.println("  help");
        System.out.println("  exit");
        System.out.println();
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println("Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.");
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
                if (a.size() < 6) {
                    System.out.println("Usage: prod addFood [<id>] \"<name>\" <price> <yyyy-MM-dd> <maxPeople>");
                    return;
                }
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
                } catch (Exception e) {
                    System.out.println("Error creating Food: " + e.getMessage());
                }
            }
            case "addMeeting" -> {
                if (a.size() < 6) {
                    System.out.println("Usage: prod addMeeting [<id>] \"<name>\" <price> <yyyy-MM-dd> <maxPeople>");
                    return;
                }
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
                } catch (Exception e) {
                    System.out.println("Error creating Meeting: " + e.getMessage());
                }
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
                        if (p instanceof ItemProduct ip) {
                            ip.setCategory(Category.valueOf(value));
                        } else {
                            System.out.println("Error: Product does not support categories");
                            return;
                        }
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
                tickets.newTicket();
                System.out.println("ticket new: ok");
            }
            case "add" -> {
                if (a.size() < 4) { System.out.println("Usage: ticket add <prodId> <quantity>"); return; }
                String prodId = a.get(2);
                int qty = Integer.parseInt(a.get(3));
                Product p = catalog.get(prodId);
                tickets.current().add(p, qty);

                Ticket t = tickets.current();

                double unitDisc = 0.0;
                if (p instanceof ItemProduct ip) {
                    unitDisc = t.unitDiscountFor(ip.getCategory(), p.getPrice());
                }

                for (int i = 0; i < qty; i++) {
                    if (unitDisc > 0)
                        System.out.println(p + " **discount -" + trim(unitDisc));
                    else
                        System.out.println(p.toString());
                }
                System.out.println("Total price: " + trim(t.totalPrice()));
                System.out.println("Total discount: " + trim(t.totalDiscount()));
                System.out.println("Final Price: " + trim(t.finalPrice()));
                System.out.println("ticket add: ok");
            }
            case "remove" -> {
                if (a.size() < 3) { System.out.println("Usage: ticket remove <prodId>"); return; }
                String prodId = a.get(2);
                tickets.current().remove(prodId);
                System.out.println("ticket remove: ok");
            }
            case "print" -> {
                Ticket t = tickets.current();
                t.getItems().forEach(li -> {
                    double unitDisc = 0.0;
                    if (li.getProduct() instanceof ItemProduct ip) {
                        unitDisc = t.unitDiscountFor(ip.getCategory(), ip.getPrice());
                    }

                    for (int i = 0; i < li.getQuantity(); i++) {
                        if (unitDisc > 0)
                            System.out.println(li.getProduct() + " **discount -" + trim(unitDisc));
                        else
                            System.out.println(li.getProduct().toString());
                    }
                });
                System.out.println("Total price: " + trim(t.totalPrice()));
                System.out.println("Total discount: " + trim(t.totalDiscount()));
                System.out.println("Final Price: " + trim(t.finalPrice()));
                System.out.println("ticket print: ok");
            }
            default -> System.out.println("Unknown ticket command");
        }
    }

    private static String trim(double v) {
        return String.format(java.util.Locale.ROOT, "%.1f", v);
    }
}