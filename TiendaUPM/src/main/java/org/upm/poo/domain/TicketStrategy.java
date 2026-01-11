package org.upm.poo.domain;

import java.util.List;

public interface TicketStrategy {
    double calculateTotalPrice(List<LineItem> items);
    double calculateTotalDiscount(List<LineItem> items);
}