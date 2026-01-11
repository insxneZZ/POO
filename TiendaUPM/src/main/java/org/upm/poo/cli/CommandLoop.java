package org.upm.poo.cli;

import org.upm.poo.domain.*;
import org.upm.poo.domain.user.Cashier;
import org.upm.poo.domain.user.Client;
import org.upm.poo.domain.user.Company;
import org.upm.poo.domain.user.Customer;
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
                        // Gestión de excepciones silenciosa en terminal (E3)
                        // "No se mostrarán por terminal errores... mecanismo de control justificado"
                        // Mostramos mensaje simple para feedback, pero no stacktrace.
                        System.out.println("Error: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Fatal Error: " + e.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Commands help available in documentation.");
    }

    // --- PRODUCTOS ---
    private void handleProd(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "add" -> {
                // prod add <exp> <category> -> E3 new format for services/prods?
                // Enunciado E3: "prod add <expiration> <category>" ? No parece encajar con el anterior.
                // Asumimos compatibilidad. Si hay fecha y categoría podría ser servicio o producto especial.
                // Revisando enunciado E3: "Se agregan Producto-Servicios... no tendrán precio... solo fecha máxima"
                // Comando E3: prod add <expiration: yyyy-MM-dd> <category>
                // OJO: El enunciado E3 tiene una errata o cambio fuerte en "prod add".
                // Asumimos que si entra expiration y category es un SERVICIO (Transporte/Espectaculo/Seguro).

                // Lógica híbrida para soportar E1/E2 y E3
                if (a.size() == 4 && isDate(a.get(2))) {
                    // E3 Service: prod add 2025-12-31 TRANSPORTE
                    String dateStr = a.get(2);
                    String catStr = a.get(3);
                    String id = generateServiceId();
                    LocalDate exp = LocalDate.parse(dateStr);

                    Service s = null;
                    if (catStr.equalsIgnoreCase("TRANSPORTE")) s = new TransportService(id, exp);
                    else if (catStr.equalsIgnoreCase("ESPECTACULO")) s = new ShowService(id, exp);
                    else if (catStr.equalsIgnoreCase("SEGURO")) s = new InsuranceService(id, exp);
                    else throw new IllegalArgumentException("Unknown service category: " + catStr);

                    catalog.add(s);
                    System.out.println(s);
                    System.out.println("prod add: ok");
                    return;
                }

                // Lógica E2 (Standard Products)
                String id = null, name; Category cat; double price; Integer maxPers = null;
                int catIdx = -1;
                if (a.size() > 3 && isCategory(a.get(3))) catIdx = 3;
                else if (a.size() > 4 && isCategory(a.get(4))) catIdx = 4;

                if (catIdx == -1) { System.out.println("Usage error: check category or params"); return; }

                if (catIdx == 4) { id = a.get(2); name = a.get(3); }
                else { id = "P-" + UUID.randomUUID().toString().substring(0, 5); name = a.get(2); }

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
                if (a.size() == 7) { id = a.get(2); offset = 1; }
                else { id = (isFood ? "F-" : "M-") + UUID.randomUUID().toString().substring(0, 5); }

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
                if (a.get(3).equals("NAME")) p.setName(val);
                else if (a.get(3).equals("PRICE")) p.setPrice(Double.parseDouble(val));
                else if (a.get(3).equals("CATEGORY") && p instanceof ItemProduct ip) ip.setCategory(Category.valueOf(val));
                System.out.println(p);
                System.out.println("prod update: ok");
            }
            case "remove" -> {
                System.out.println(catalog.remove(a.get(2)));
                System.out.println("prod remove: ok");
            }
        }
    }

    // --- TICKETS ---
    private void handleTicket(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "new" -> {
                // ticket new [<id>] <cashId> <userId> -[c/p/s]
                // Detectar si hay ID opcional
                String id = null, cashId, userId, flag = "-p";

                int idx = 2;
                if (!userRegistry.listCashiers().stream().anyMatch(c->c.getId().equals(a.get(2))) && a.size() >= 5) {
                    id = a.get(idx++);
                }
                cashId = a.get(idx++);
                userId = a.get(idx++);
                if (idx < a.size()) flag = a.get(idx);

                Cashier c = userRegistry.getCashier(cashId);
                Customer cust = userRegistry.getCustomer(userId);

                Ticket<?> t = tickets.createTicket(id, cashId, userId, flag);

                c.addTicketId(t.getId());
                cust.addTicketId(t.getId());

                // Impresión de estado usando Printer
                getPrinter(t).print(t);
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

                // Raw casting seguro porque el ticket valida internamente
                ((Ticket)t).add(p, qty, customs);

                getPrinter(t).print(t);
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
                getPrinter(t).print(t);
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

    private ITicketPrinter getPrinter(Ticket<?> t) {
        if (t instanceof EnterpriseTicket) return new EnterpriseTicketPrinter();
        return new StandardTicketPrinter();
    }

    // --- USUARIOS ---
    private void handleClient(List<String> a) {
        if (a.size() < 2) return;
        switch (a.get(1)) {
            case "add" -> {
                // client add "<name>" (<DNI>|<NIF>) <email> <cashld>
                String name = a.get(2);
                String id = a.get(3); // DNI or NIF
                String email = a.get(4);
                String cashId = a.get(5);

                // Detección automática por formato
                // NIF: 8 dígitos + Letra (Empresa) -> No, NIF empresa suele empezar por letra.
                // Enunciado E3: "identificados por NIF".
                // Asumiremos convención simple: Si empieza por letra -> Empresa, Si empieza por número -> Cliente (DNI).
                // O según PDF E3 "aceptara NIF y decidirá el tipo de usuario".

                boolean isCompany = Character.isLetter(id.charAt(0));

                if (isCompany) {
                    Company c = userRegistry.addCompany(new Company(id, name, email, cashId));
                    System.out.println(c);
                } else {
                    Client c = userRegistry.addClient(new Client(id, name, email, cashId));
                    System.out.println(c);
                }
                System.out.println("client add: ok");
            }
            case "remove" -> {
                userRegistry.removeCustomer(a.get(2));
                System.out.println("client remove: ok");
            }
            case "list" -> {
                System.out.println("Client:");
                userRegistry.listClients().forEach(c -> System.out.println("  " + c));
                userRegistry.listCompanies().forEach(c -> System.out.println("  " + c));
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
            case "remove" -> {
                tickets.removeTicketsByCashier(a.get(2));
                userRegistry.removeCashier(a.get(2));
                System.out.println("cash remove: ok");
            }
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

    private boolean isCategory(String s) { try { Category.valueOf(s); return true; } catch (Exception e) { return false; } }
    private boolean isDate(String s) { try { LocalDate.parse(s); return true; } catch (Exception e) { return false; } }

    private int serviceSeq = 1;
    private String generateServiceId() { return (serviceSeq++) + "S"; }
}