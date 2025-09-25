package org.upm.poo.service;

import org.upm.poo.domain.Ticket;

public final class TicketService {
    private Ticket current = new Ticket();
    public void newTicket() { current = new Ticket(); }
    public Ticket current() { return current; }
}
