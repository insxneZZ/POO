package org.upm.poo.domain.user;

public class Company extends Customer {

    private static final String NIF_REGEX = "^[0-9]{8}[A-HJ-NP-TV-Z]$";

    public Company(String nif, String name, String email, String creatorCashierId) {
        super(nif, name, email, creatorCashierId);

        if (!nif.toUpperCase().matches(NIF_REGEX)) {
            throw new IllegalArgumentException("Formato de NIF no válido: " + nif);
        }
    }

    public String getNif() {
        return getId();
    }
}