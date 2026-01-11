package org.upm.poo.domain;

import java.time.LocalDate;

public class ShowService extends Service {
    public ShowService(String id, LocalDate maxUsageDate) {
        super(id, "Espectáculo", maxUsageDate);
    }
}