package org.upm.poo.domain.user;

import java.util.Objects;

public final class Client {
    private final String id;
    private String name;
    private String email;
    private final String creatorCashierId;
    private final boolean isCompany;

    public Client(String id, String name, String email, String creatorCashierId) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("ID (DNI/NIF) required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email required");
        if (creatorCashierId == null || creatorCashierId.isBlank()) throw new IllegalArgumentException("Creator Cashier ID required");

        this.id = id;
        this.name = name;
        this.email = email;
        this.creatorCashierId = creatorCashierId;
        this.isCompany = detectCompany(id);
    }

    private boolean detectCompany(String id) {
        if (id == null || id.isBlank()) return false;
        String upperId = id.toUpperCase();
        char firstChar = upperId.charAt(0);

        if (Character.isDigit(firstChar)) return false;

        if (firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z') return false;

        return true;
    }

    public boolean isCompany() {
        return isCompany;
    }

    public String getDni() { return id; }
    public String getId() { return id; } // Alias útil

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCreatorCashierId() { return creatorCashierId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client client)) return false;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        String type = isCompany ? " [CORP]" : "";
        return "Client" + type + "{identifier='" + getId() + "', name='" + getName() + "', email='" + getEmail() + "', cash=" + getCreatorCashierId() + "}";
    }
}