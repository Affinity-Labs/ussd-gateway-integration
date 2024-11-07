package com.affinitylabs.services;

import com.affinitylabs.models.middleware.auth.ClientLoginRequest;
import com.affinitylabs.models.middleware.auth.ClientLoginResponse;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@ApplicationScoped
public class AuthServer {

    @RestClient
    AuthServerClient authServerClient;


    @CacheResult(cacheName = "customer-token")
    public ClientLoginResponse customerLogin(ClientLoginRequest clientLoginRequest, @CacheKey String sessionId) {
        Log.info("Client login request: " + clientLoginRequest.phoneNumber());
        return authServerClient.customerLogin(clientLoginRequest);
    }
}
