package org.upm.poo;

import org.upm.poo.cli.CommandLoop;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.TicketService;
import org.upm.poo.service.UserRegistry;

public final class Main {
    public static void main(String[] args) throws Exception {
        Catalog catalog = new Catalog();
        TicketService ticketService = new TicketService();
        UserRegistry userRegistry = new UserRegistry();

        CommandLoop cli = new CommandLoop(catalog, ticketService, userRegistry);

        if (args.length > 0) {
            cli.run(args[0]);
        } else {
            cli.run(null);
        }
    }
}