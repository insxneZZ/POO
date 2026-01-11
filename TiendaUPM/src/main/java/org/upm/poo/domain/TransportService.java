package org.upm.poo.domain;

import java.time.LocalDate;

public class TransportService extends Service {
    public TransportService(String id, LocalDate maxUsageDate) {
        super(id, "Transporte", maxUsageDate);
    }
}