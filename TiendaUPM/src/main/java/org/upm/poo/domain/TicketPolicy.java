package org.upm.poo.domain;

public interface TicketPolicy {
    void checkAddition(Product p);

    double calculateFinalPrice(Ticket t);

    double calculateTotalDiscount(Ticket t);

    String formatLineInfo(LineItem li);

    void validateClosing(Ticket t);

    void printTicketInfo(Ticket t);
}