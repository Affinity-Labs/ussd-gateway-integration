package com.affinitylabs.services;

import com.affinitylabs.exceptions.AuthException;
import com.affinitylabs.exceptions.ServerErrorException;
import com.affinitylabs.models.middleware.auth.ClientLoginRequest;
import com.affinitylabs.models.middleware.auth.ClientLoginResponse;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "auth-server-client")
public interface AuthServerClient {

    @ClientExceptionMapper
    static RuntimeException toException(Response response) throws AuthException {
        if (response.getStatus() == 500) {
            return new ServerErrorException("Server Error");
        } else if (response.getStatus() >= 400 && response.getStatus() <= 499) {
            return new AuthException("Unauthorized");
        }
        return null;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/api/client/auth/login")
    @ClientHeaderParam(name = "X-AFFINITY-SOURCE-KEY", value = "${AUTH_SERVER_SOURCE_KEY}")
    ClientLoginResponse customerLogin(ClientLoginRequest clientLoginRequest);
}
