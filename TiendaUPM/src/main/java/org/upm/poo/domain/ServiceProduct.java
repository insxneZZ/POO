package org.upm.poo.domain;

import java.time.LocalDate;

public class ServiceProduct extends Product {
    private final LocalDate expiration;
    private final Category category;

    public ServiceProduct(String id, LocalDate expiration, Category category) {
        super(id, "Service", 0.0);
        this.expiration = expiration;
        this.category = category;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "{class:ProductService, id:" + getId() + ", category:" + category + ", expiration:" + expiration + "}";
    }
}