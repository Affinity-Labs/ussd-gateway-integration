package com.affinitylabs.models.middleware.accounts;

public record Funding(String source, float amount, String phoneNumber, String mobileOperator, String sourceAccountNumber, String idempotentKey ) {
}
