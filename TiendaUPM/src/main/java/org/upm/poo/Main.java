package org.upm.poo;

import org.upm.poo.cli.CommandLoop;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.PersistenceService;
import org.upm.poo.service.TicketService;
import org.upm.poo.service.UserRegistry;

public class Main {
    public static void main(String[] args) {
        Catalog catalog = new Catalog();
        UserRegistry userRegistry = new UserRegistry();
        TicketService ticketService = new TicketService(userRegistry);

        PersistenceService persistence = new PersistenceService();
        persistence.load(catalog, userRegistry, ticketService);

        CommandLoop app = new CommandLoop(catalog, ticketService, userRegistry);

        String inputPath = (args.length > 0) ? args[0] : null;
        app.run(inputPath);

        persistence.save(catalog, userRegistry, ticketService);
    }
}