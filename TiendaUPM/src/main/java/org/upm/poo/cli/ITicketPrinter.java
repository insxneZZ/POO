package org.upm.poo.cli;

import org.upm.poo.domain.Ticket;

public interface ITicketPrinter {
    void print(Ticket<?> ticket);
}