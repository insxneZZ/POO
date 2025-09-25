package org.upm.poo;

import org.upm.poo.cli.CommandLoop;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.TicketService;

public final class Main {
    public static void main(String[] args) throws Exception {
        new CommandLoop(new Catalog(), new TicketService()).run();
    }
}
