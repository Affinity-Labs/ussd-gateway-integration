package com.affinitylabs.models.middleware.transactions;

import java.time.LocalDate;

public record AccountStatementRequest(LocalDate startDate, LocalDate endDate, String channel, String accountNumber) {
}
