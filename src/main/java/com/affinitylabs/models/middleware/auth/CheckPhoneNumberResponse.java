package com.affinitylabs.models.middleware.auth;

public record CheckPhoneNumberResponse(String userId, String status, boolean isPrivateBetaUser) {
}
