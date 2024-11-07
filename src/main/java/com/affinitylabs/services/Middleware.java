package com.affinitylabs.services;

import com.affinitylabs.exceptions.AuthException;
import com.affinitylabs.exceptions.BadRequestException;
import com.affinitylabs.models.middleware.accounts.AccountBalance;
import com.affinitylabs.models.middleware.accounts.ClientAccountData;
import com.affinitylabs.models.middleware.accounts.Customer;
import com.affinitylabs.models.middleware.accounts.NewAccount;
import com.affinitylabs.models.middleware.auth.*;
import com.affinitylabs.models.middleware.transactions.AccountStatementRequest;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;


@ApplicationScoped
public class Middleware {

    @RestClient
    MiddlewareClient middlewareClient;

    public ValidatePasswordResponse validateCustomer(ClientLoginRequest clientLoginRequest, String sessionId) {
        try{
            Log.info("Validating login requested" + clientLoginRequest.phoneNumber());
            return middlewareClient.validateCustomer(clientLoginRequest);
        } catch (BadRequestException e) {
            throw new AuthException(e.getLocalizedMessage());
        }

    }

    public CheckPhoneNumberResponse userStatus(CheckPhoneNumberRequest checkPhoneNumberRequest) {
        Log.info("Check PhoneNumber Status");
        return middlewareClient.checkPhoneStatus(checkPhoneNumberRequest);
    }

    public Customer getCustomer(@CacheKey String phoneNumber) {
        Log.info("Get Customer Details Request");
        return middlewareClient.getCustomer(phoneNumber);
    }


    @CacheResult(cacheName = "customer-details")
    public ClientAccountData getCustomerAccounts(String token, @CacheKey String phoneNumber){
        Log.info("Get Customer Account Request");
        return middlewareClient.getAccounts(token);
    }

    public AccountBalance getAccountBalance(String accountNumber) {
        Log.info("Get Account Balance Request");
        return middlewareClient.getAccountBalance(accountNumber);
    }

    public void accountStatement(AccountStatementRequest accountStatementRequest){
        Log.info("Account Statement Request");
        middlewareClient.accountStatement(accountStatementRequest);
    }

    public void createNewAccount(String token, NewAccount newAccount){
        Log.info("New Account Request");
        middlewareClient.addNewAccount(token, newAccount);
    }

    public void requestCertificate(String accNumber, String token){
        Log.info("Request Certificate Request");
        middlewareClient.getCertificate(accNumber, token);
    }


}