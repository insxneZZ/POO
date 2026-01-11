package org.upm.poo.domain;

import java.time.LocalDate;

public class InsuranceService extends Service {
    public InsuranceService(String id, LocalDate maxUsageDate) {
        super(id, "Seguro", maxUsageDate);
    }
}