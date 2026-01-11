package org.upm.poo.domain;

import java.time.LocalDate;

public class Service extends Product {
    private final ServiceType type;
    private final LocalDate expirationDate;

    public Service(String id, ServiceType type, LocalDate expirationDate) {
        super(id, type.name(), 0.0);
        this.type = type;
        this.expirationDate = expirationDate;
    }

    public ServiceType getType() { return type; }
    public LocalDate getExpirationDate() { return expirationDate; }

    @Override
    public String toString() {
        return "{class:Service, id:" + getId() + ", type:" + type + ", expires:" + expirationDate + "}";
    }
}