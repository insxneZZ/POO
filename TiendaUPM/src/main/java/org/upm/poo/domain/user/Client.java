package org.upm.poo.domain.user;

import java.util.Objects;

public final class Client {
    private final String dni;
    private String name;
    private String email;
    private final String creatorCashierId;

    public Client(String dni, String name, String email, String creatorCashierId) {
        if (dni == null || dni.isBlank()) throw new IllegalArgumentException("DNI required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email required");
        if (creatorCashierId == null || creatorCashierId.isBlank()) throw new IllegalArgumentException("Creator Cashier ID required");

        this.dni = dni;
        this.name = name;
        this.email = email;
        this.creatorCashierId = creatorCashierId;
    }

    public String getDni() { return dni; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCreatorCashierId() { return creatorCashierId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(dni, client.dni);
    }

    @Override
    public int hashCode() { return Objects.hash(dni); }

    @Override
    public String toString() {
        return "Client{identifier='" + getDni() + "', name='" + getName() + "', email='" + getEmail() + "', cash=" + getCreatorCashierId() + "}";
    }
}