package com.affinitylabs.services;

import com.affinitylabs.exceptions.AuthException;
import com.affinitylabs.exceptions.BadRequestException;
import com.affinitylabs.exceptions.ServerErrorException;
import com.affinitylabs.models.middleware.transactions.BankListResponse;
import com.affinitylabs.models.middleware.transactions.QueryServiceRequest;
import com.affinitylabs.models.middleware.transactions.QueryServiceResponse;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "transaction-client")
@Produces(MediaType.APPLICATION_JSON)
public interface TransactionClient {
    @ClientExceptionMapper
    static RuntimeException toException(Response response) throws AuthException {
        if (response.getStatus() == 500) {
            return new ServerErrorException("Server Error");
        }
        else if (response.getStatus() >= 400 && response.getStatus() <= 499) {
            return new BadRequestException("Bad Request");
        }
        return null;
    }

    @POST
    @Path("/api/client/transactions/service/query")
    QueryServiceResponse queryService(@HeaderParam("x-api-key") String token, QueryServiceRequest queryServiceRequest);

    @GET
    @Path("/api/client/utilities/banks")
    List<BankListResponse> bankList(@HeaderParam("x-api-key") String token, @QueryParam("serviceCode") String serviceCode);

}
