package com.affinitylabs.models.middleware.auth;

import java.time.LocalDateTime;

public record ClientLoginResponse(UserResponse user, String token, LocalDateTime expiresAt) {
}
