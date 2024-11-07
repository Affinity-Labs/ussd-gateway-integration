package com.affinitylabs.models.middleware.auth;

import java.util.List;
import java.util.Map;

public record UserResponse(String id,
                           String firstName,
                           String lastName,
                           String otherNames,
                           String name,
                           String email,
                           String phoneNumber,
                           String emailVerifiedAt,
                           String externalCustomerId,
                           String createdAt,
                           String updatedAt,
                           List<Map<String, String>> roles,
                           List<Map<String, String>> permissions
) {
}
