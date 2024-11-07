package com.affinitylabs.models.middleware.transactions;

public record QueryServiceRequest(String serviceCode, String accountNumber, String gipCode) {
}
