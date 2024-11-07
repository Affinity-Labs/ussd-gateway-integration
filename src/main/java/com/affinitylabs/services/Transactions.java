package com.affinitylabs.services;

import com.affinitylabs.models.middleware.transactions.QueryServiceRequest;
import com.affinitylabs.models.middleware.transactions.QueryServiceResponse;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class Transactions {

    @RestClient
    TransactionClient transactionClient;

    public QueryServiceResponse queryService(String token, QueryServiceRequest queryServiceRequest) {
        return transactionClient.queryService(token, queryServiceRequest);
    }
}
