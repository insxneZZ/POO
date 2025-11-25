package org.upm.poo;

import org.upm.poo.cli.CommandLoop;
import org.upm.poo.service.Catalog;
import org.upm.poo.service.TicketService;
import org.upm.poo.service.UserRegistry;

public final class Main {
    public static void main(String[] args) throws Exception {
        new CommandLoop(new Catalog(), new TicketService(), new UserRegistry()).run();
    }
}