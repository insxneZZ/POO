package org.upm.poo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class Food extends EventProduct {

    public Food(String id, String name, double price, LocalDate expiration, int maxPeople) {
        super(id, name, price, expiration, maxPeople);
        validatePlanningTime(LocalDateTime.now());
    }

    @Override
    public void validatePlanningTime(LocalDateTime contextDate) {
        long days = ChronoUnit.DAYS.between(contextDate.toLocalDate(), getExpiration());
        if (days < 3) {
            throw new IllegalArgumentException("Food orders require at least 3 days planning.");
        }
    }
}