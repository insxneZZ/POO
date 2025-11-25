package org.upm.poo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class EventProduct extends Product {
    private final LocalDate expiration;
    private final int maxPeople;

    public EventProduct(String id, String name, double price, LocalDate expiration, int maxPeople) {
        super(id, name, price);
        if (expiration == null) throw new IllegalArgumentException("expiration date required");
        if (maxPeople <= 0) throw new IllegalArgumentException("maxPeople must be > 0");
        if (maxPeople > 100) throw new IllegalArgumentException("maxPeople cannot exceed 100");

        this.expiration = expiration;
        this.maxPeople = maxPeople;

        if (expiration.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot create an event in the past");
        }
    }

    public LocalDate getExpiration() { return expiration; }
    public int getMaxPeople() { return maxPeople; }

    /**
     * Valída si la fecha del evento cumple con el tiempo mínimo de planificación
     * respecto a una fecha de referencia (normalmente 'ahora' o fecha del ticket).
     */
    public abstract void validatePlanningTime(LocalDateTime contextDate);

    @Override
    public String toString() {
        return "{class:" + this.getClass().getSimpleName() +
                ", id:" + getId() +
                ", name:'" + getName() +
                "', price:" + getPrice() +
                ", expiration:" + expiration +
                ", maxPeople:" + maxPeople + "}";
    }
}