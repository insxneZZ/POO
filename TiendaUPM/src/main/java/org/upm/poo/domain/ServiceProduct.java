package org.upm.poo.domain;

import java.time.LocalDate;

public class ServiceProduct extends Product {
    private final LocalDate expiration;

    public ServiceProduct(String id, LocalDate expiration) {
        super(id, "Service", 0.0);
        this.expiration = expiration;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    @Override
    public String toString() {
        return "{class:ServiceProduct, id:" + getId() + ", expiration:" + expiration + "}";
    }
}