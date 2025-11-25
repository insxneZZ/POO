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

    public void run(String inputFilePath) {
        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");

        boolean isFileMode = (inputFilePath != null);
        Reader inputReader;

        try {
            inputReader = isFileMode ? new FileReader(inputFilePath) : new InputStreamReader(System.in);
            try (BufferedReader br = new BufferedReader(inputReader)) {
                while (true) {
                    if (!isFileMode) System.out.print("tUPM> ");
                    String line = br.readLine();
                    if (line == null) break;

                    line = line.trim();
                    if (line.isBlank()) continue;

                    if (isFileMode) System.out.println("tUPM> " + line);

                    try {
                        if (line.equals("help")) { printHelp(); continue; }
                        if (line.startsWith("echo ")) {
                            System.out.println(line.substring(5).replace("\"", ""));
                            continue;
                        }
                        if (line.equals("exit")) {
                            System.out.println("Closing application.");
                            System.out.println("Goodbye!");
                            break;
                        }

                        List<String> a = CommandParser.splitArgs(line);
                        if (a.isEmpty()) continue;

                        switch (a.get(0)) {
                            case "prod"   -> handleProd(a);
                            case "ticket" -> handleTicket(a);
                            case "client" -> handleClient(a);
                            case "cash"   -> handleCash(a);
                            default       -> System.out.println("Unknown command. Type 'help'.");
                        }
                    } catch (Exception ex) {
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Fatal Error: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  prod add ... / addFood ... / addMeeting ... / list / update / remove");
        System.out.println("  client add ... / remove / list");
        System.out.println("  cash add ... / remove / list / tickets");
        System.out.println("  ticket new / add / remove / print / list");
        System.out.println("  help / exit / echo");
    }

    // --- PRODUCTOS ---
    private void handleProd(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "add" -> {
                String id = null, name; Category cat; double price; Integer maxPers = null;

                int catIdx = -1;
                if (a.size() > 3 && isCategory(a.get(3))) catIdx = 3;
                else if (a.size() > 4 && isCategory(a.get(4))) catIdx = 4;

                if (catIdx == -1) { System.out.println("Usage error: check category"); return; }

                if (catIdx == 4) {
                    id = a.get(2); name = a.get(3);
                } else {
                    id = "P-" + UUID.randomUUID().toString().substring(0, 5);
                    name = a.get(2);
                }

                cat = Category.valueOf(a.get(catIdx));
                price = Double.parseDouble(a.get(catIdx + 1));
                if (a.size() > catIdx + 2) maxPers = Integer.parseInt(a.get(catIdx + 2));

                Product p = (maxPers != null) ?
                        new CustomizableProduct(id, name, cat, price, maxPers) :
                        new StandardProduct(id, name, cat, price);

                catalog.add(p);
                System.out.println(p);
                System.out.println("prod add: ok");
            }
            case "addFood", "addMeeting" -> {
                boolean isFood = a.get(1).equals("addFood");
                String id, name; double price; LocalDate exp; int maxP;

                int offset = 0;
                if (a.size() == 7) {
                    id = a.get(2); offset = 1;
                } else {
                    id = (isFood ? "F-" : "M-") + UUID.randomUUID().toString().substring(0, 5);
                }

                name = a.get(2 + offset);
                price = Double.parseDouble(a.get(3 + offset));
                exp = LocalDate.parse(a.get(4 + offset));
                maxP = Integer.parseInt(a.get(5 + offset));

                Product p = isFood ? new Food(id, name, price, exp, maxP) : new Meeting(id, name, price, exp, maxP);
                catalog.add(p);
                System.out.println(p);
                System.out.println("prod " + a.get(1) + ": ok");
            }
            case "list" -> {
                System.out.println("Catalog:");
                catalog.list().forEach(p -> System.out.println("  " + p));
                System.out.println("prod list: ok");
            }
            case "update" -> {
                Product p = catalog.get(a.get(2));
                String val = a.get(4);
                switch (a.get(3)) {
                    case "NAME" -> p.setName(val);
                    case "PRICE" -> p.setPrice(Double.parseDouble(val));
                    case "CATEGORY" -> { if(p instanceof ItemProduct ip) ip.setCategory(Category.valueOf(val)); }
                }
                System.out.println(p);
                System.out.println("prod update: ok");
            }
            case "remove" -> {
                System.out.println(catalog.remove(a.get(2)));
                System.out.println("prod remove: ok");
            }
        }
    }

    private boolean isCategory(String s) { try { Category.valueOf(s); return true; } catch (Exception e) { return false; } }

    // --- TICKETS ---
    private void handleTicket(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "new" -> {
                String id = null, cashId, userId;
                if (a.size() == 5) { id = a.get(2); cashId = a.get(3); userId = a.get(4); }
                else { cashId = a.get(2); userId = a.get(3); }

                Ticket t = tickets.createTicket(id, cashId, userId);
                printTicketState(t);
                System.out.println("ticket new: ok");
            }
            case "add" -> {
                String tId = a.get(2); String cashId = a.get(3); String pId = a.get(4); int qty = Integer.parseInt(a.get(5));

                List<String> customs = new ArrayList<>();
                for (int i = 6; i < a.size(); i++) {
                    String arg = a.get(i);
                    if (arg.startsWith("--p")) {
                        if (arg.equals("--p") && i + 1 < a.size()) { customs.add(a.get(i + 1)); i++; }
                        else if (arg.length() > 3) customs.add(arg.substring(3));
                    }
                }

                Ticket t = tickets.getTicket(tId);
                tickets.verifyOwner(t, cashId);
                Product p = catalog.get(pId);
                t.add(p, qty, customs);

                printTicketHeader(t);

                double base = p.getPrice();
                double extra = (!customs.isEmpty()) ? base * 0.10 * customs.size() : 0.0;
                double finalU = base + extra;
                double uDisc = 0.0;
                if (p instanceof ItemProduct ip) uDisc = t.unitDiscountFor(ip.getCategory(), finalU);

                for(int i = 0; i < qty; i++) {
                    String info = p.toString();
                    if (!customs.isEmpty()) info += " " + customs;

                    if (uDisc > 0) System.out.println("  " + info + " **discount -" + trim(uDisc));
                    else System.out.println("  " + info);
                }
                printTicketTotals(t);
                System.out.println("ticket add: ok");
            }
            case "remove" -> {
                Ticket t = tickets.getTicket(a.get(2));
                tickets.verifyOwner(t, a.get(3));
                t.remove(a.get(4));
                System.out.println("ticket remove: ok");
            }
            case "print" -> {
                Ticket t = tickets.getTicket(a.get(2));
                tickets.verifyOwner(t, a.get(3));
                t.close();
                printTicketDetails(t);
                System.out.println("ticket print: ok");
            }
            case "list" -> {
                System.out.println("Ticket List:");
                tickets.findAll().stream()
                        .sorted((t1, t2) -> t1.getId().compareTo(t2.getId()))
                        .forEach(t -> {
                            String st = switch(t.getState()) {
                                case EMPTY -> "EMPTY";
                                case ACTIVE -> "OPEN";
                                case CLOSED -> "CLOSE";
                            };
                            System.out.println("  " + t.getId() + " - " + st);
                        });
                System.out.println("ticket list: ok");
            }
        }
    }

    private void printTicketState(Ticket t) {
        printTicketHeader(t);
        printTicketTotals(t);
    }

    private void printTicketHeader(Ticket t) {
        System.out.println("Ticket : " + t.getId());
    }

    private void printTicketTotals(Ticket t) {
        System.out.println("  Total price: " + trim(t.totalPrice()));
        System.out.println("  Total discount: " + trim(t.totalDiscount()));
        System.out.println("  Final Price: " + trim(t.finalPrice()));
    }

    private void printTicketDetails(Ticket t) {
        printTicketHeader(t);
        t.getItems().stream()
                .sorted((l1, l2) -> l1.getProduct().getName().compareToIgnoreCase(l2.getProduct().getName()))
                .forEach(li -> {
                    Product p = li.getProduct();

                    double base = p.getPrice();
                    double extra = (!li.getCustomizations().isEmpty()) ? base * 0.10 * li.getCustomizations().size() : 0.0;
                    double finalU = base + extra;

                    double uDisc = 0.0;
                    if (p instanceof ItemProduct ip) uDisc = t.unitDiscountFor(ip.getCategory(), finalU);

                    for(int i=0; i<li.getQuantity(); i++) {
                        String info = p.toString();
                        if(!li.getCustomizations().isEmpty()) info += " " + li.getCustomizations();

                        if (uDisc > 0) System.out.println("  " + info + " **discount -" + trim(uDisc));
                        else System.out.println("  " + info);
                    }
                });
        printTicketTotals(t);
    }

    // --- USUARIOS ---
    private void handleClient(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "add" -> {
                Client c = userRegistry.addClient(new Client(a.get(3), a.get(2), a.get(4), a.get(5)));
                System.out.println(c); System.out.println("client add: ok");
            }
            case "remove" -> { userRegistry.removeClient(a.get(2)); System.out.println("client remove: ok"); }
            case "list" -> {
                System.out.println("Client:");
                userRegistry.listClients().forEach(c -> System.out.println("  " + c));
                System.out.println("client list: ok");
            }
        }
    }

    private void handleCash(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "add" -> {
                String id = (a.size() == 5) ? a.get(2) : null;
                String name = (a.size() == 5) ? a.get(3) : a.get(2);
                String email = (a.size() == 5) ? a.get(4) : a.get(3);
                Cashier c = userRegistry.addCashier(new Cashier(id, name, email));
                System.out.println(c); System.out.println("cash add: ok");
            }
            case "remove" -> { userRegistry.removeCashier(a.get(2)); System.out.println("cash remove: ok"); }
            case "list" -> {
                System.out.println("Cash:");
                userRegistry.listCashiers().forEach(c -> System.out.println("  " + c));
                System.out.println("cash list: ok");
            }
            case "tickets" -> {
                String cId = a.get(2);
                System.out.println("Tickets: ");
                tickets.findAll().stream()
                        .filter(t -> t.getCashierId().equals(cId))
                        .forEach(t -> {
                            String st = (t.getState() == TicketState.EMPTY) ? "EMPTY" : (t.getState()==TicketState.CLOSED?"CLOSE":"OPEN");
                            System.out.println("  " + t.getId() + "->" + st);
                        });
                System.out.println("cash tickets: ok");
            }
        }
    }

    private static String trim(double v) { return String.format(java.util.Locale.ROOT, "%.1f", v); }
}