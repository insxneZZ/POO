package org.upm.poo.domain.user;

import java.util.Objects;
import java.util.Random;

public final class Cashier {
    private final String id;
    private String name;
    private String email;

    public Cashier(String id, String name, String email) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");

        this.id = (id == null || id.isBlank()) ? generateId() : id;
        this.name = name;
        this.email = email;

        if (!this.id.matches("UW\\d{7}")) {
            throw new IllegalArgumentException("Cashier ID must follow pattern UWxxxxxxx");
        }
    }

    private String generateId() {
        Random r = new Random();
        int num = 1000000 + r.nextInt(9000000);
        return "UW" + num;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cashier cashier)) return false;
        return Objects.equals(id, cashier.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Cash{identifier='" + getId() + "', name='" + getName() + "', email='" + getEmail() + "'}";
    }
}