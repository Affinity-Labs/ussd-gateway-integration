package com.affinitylabs.models.middleware.transactions;

public record BankListResponse(String gipCode, String name, String serviceCode, String type, boolean isActive, String icon) {
}
