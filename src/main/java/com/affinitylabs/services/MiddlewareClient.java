package com.affinitylabs.services;

import com.affinitylabs.exceptions.AuthException;
import com.affinitylabs.exceptions.BadRequestException;
import com.affinitylabs.exceptions.ServerErrorException;
import com.affinitylabs.models.middleware.accounts.AccountBalance;
import com.affinitylabs.models.middleware.accounts.ClientAccountData;
import com.affinitylabs.models.middleware.accounts.Customer;
import com.affinitylabs.models.middleware.accounts.NewAccount;
import com.affinitylabs.models.middleware.auth.*;
import com.affinitylabs.models.middleware.transactions.AccountStatementRequest;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;


@RegisterRestClient(configKey = "middleware-client")

@Produces(MediaType.APPLICATION_JSON)
public interface MiddlewareClient {
    @ClientExceptionMapper
    static RuntimeException toException(Response response) throws AuthException {
        if (response.getStatus() == 500) {
            return new ServerErrorException("Server Error");
        }
        else if (response.getStatus() >= 400 && response.getStatus() <= 499) {
            System.out.println(response.getStatus());
            return new BadRequestException("Bad Request");
        }
        return null;
    }

    @POST
    @ClientHeaderParam(name = "x-api-key", value = "${AUTH_SERVER_SOURCE_KEY}")
    @Path("/auth/ussd/validate-password")
    ValidatePasswordResponse validateCustomer(ClientLoginRequest clientLoginRequest);

    @POST
    @Path("/auth/onboarding/client/check-phone-number")
    @ClientHeaderParam(name = "x-api-key", value = "${AUTH_SERVER_SOURCE_KEY}")
    @ClientQueryParam(name="channel", value = "USSD")
    CheckPhoneNumberResponse checkPhoneStatus(CheckPhoneNumberRequest checkPhoneNumberRequest);


    @GET
    @Path("/account/backoffice/ussd/customer")
    @ClientHeaderParam(name = "x-api-key", value = "${AUTH_SERVER_SOURCE_KEY}")
    Customer getCustomer(@QueryParam("phoneNumber") String phoneNumber);

    @GET
    @Path("/account/client/all")
    ClientAccountData getAccounts(@HeaderParam("x-api-key") String token);

    @GET
    @ClientHeaderParam(name = "x-api-key", value = "${AUTH_SERVER_SOURCE_KEY}")
    @Path("/account/backoffice/ussd/account/{accNum}/available-balance")
    AccountBalance getAccountBalance(@PathParam("accNum") String accNum);

    @POST
    @ClientHeaderParam(name = "x-api-key", value = "${AUTH_SERVER_SOURCE_KEY}")
    @Path("/transactions/client/statement")
    String accountStatement(AccountStatementRequest accountStatementRequest);

    @POST
    @Path("/account/client/add")
    String addNewAccount(@HeaderParam("x-api-key") String token, NewAccount newAccount);

    @GET
    @Path("/account/client/future-account/{accountNumber}/certificate")
    String getCertificate(@PathParam("accountNumber") String accountNumber, @HeaderParam("x-api-key") String token);

}