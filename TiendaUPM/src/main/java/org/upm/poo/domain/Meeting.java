package org.upm.poo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class Meeting extends EventProduct {

    public Meeting(String id, String name, double price, LocalDate expiration, int maxPeople) {
        super(id, name, price, expiration, maxPeople);
        validatePlanningTime(LocalDateTime.now());
    }

    @Override
    public void validatePlanningTime(LocalDateTime contextDate) {
        LocalDateTime eventStart = getExpiration().atStartOfDay();
        long hours = ChronoUnit.HOURS.between(contextDate, eventStart);

        if (hours < 12) {
            throw new IllegalArgumentException("Meeting requires at least 12 hours planning.");
        }
    }
}