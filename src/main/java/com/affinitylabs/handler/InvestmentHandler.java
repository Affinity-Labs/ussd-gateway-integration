package com.affinitylabs.handler;

import com.affinitylabs.exceptions.BadRequestException;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.middleware.accounts.ClientAccountData;
import com.affinitylabs.models.middleware.accounts.Customer;
import com.affinitylabs.models.middleware.auth.ClientLoginResponse;
import com.affinitylabs.models.session.Menu;
import com.affinitylabs.models.session.MenuSession;
import com.affinitylabs.services.Middleware;
import com.affinitylabs.utilities.CacheKeysService;
import com.affinitylabs.utilities.Helper;
import com.affinitylabs.views.GenericViews;
import com.affinitylabs.views.InvestmentView;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class InvestmentHandler {

    @Inject
    Middleware middleware;

    @Inject
    Helper helper;

    @Inject
    InvestmentView investmentView;

    @Inject
    CacheKeysService cacheKeysService;

    @Inject
    GenericViews genericViews;


    public BaseResponse handler(BaseRequest baseRequest) {
        MenuSession currentMenuSession = MenuSession.findBySessionId(baseRequest.sessionId());
        switch (currentMenuSession.nextMenu){
            case INVESTMENTS -> {
                try{
                    ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                    ClientAccountData customerDetails = middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
                    currentMenuSession.nextMenu = Menu.SELECT_ACCOUNT;
                    Map<String, String> customerAccounts = helper.futureAccountMapper(customerDetails);
                    currentMenuSession.numberOfAccounts = customerAccounts.size();
                    currentMenuSession.persistOrUpdate();
                    if (customerAccounts.size() > 4){
                        Map<String, String> paginatedAccount = helper.paginatedAccount(customerAccounts);
                        Map<String, String> accountLeft =  helper.accountLeftMapper(customerAccounts, paginatedAccount);
                        cacheKeysService.initializeCacheLeft(baseRequest.sessionId(), accountLeft);
                        cacheKeysService.initializeCacheAccount(baseRequest.sessionId(), paginatedAccount);
                        return investmentView.affinityAccount(paginatedAccount, new int[]{2}, "#. Next");
                    }
                    return investmentView.affinityAccount(helper.futureAccountMapper(customerDetails), new int[]{2}, null);
                } catch (InterruptedException | ExecutionException e){
                    Log.error(e.getLocalizedMessage(), e);
                    return genericViews.serverError();
                }

            }
            case SELECT_ACCOUNT -> {
                try {
                    if (baseRequest.userInput().equals("#")) {
                        Map<String, String> customerAccounts = cacheKeysService.getAccountLeft(baseRequest.sessionId()).get();
                        int numberOfAccounts = currentMenuSession.numberOfAccounts;
                        int numberOfAccountsLeft = customerAccounts.size();
                        int count = numberOfAccounts - numberOfAccountsLeft + 1;
                        Map<String, String> newPaginatedAccount = helper.paginatedAccount(customerAccounts);
                        Map<String, String> newCustomerAccount = helper.accountLeftMapper(customerAccounts, newPaginatedAccount);
                        cacheKeysService.initializeCacheLeft(baseRequest.sessionId(), newCustomerAccount);
                        cacheKeysService.initializeCacheAccount(baseRequest.sessionId(), newPaginatedAccount);
                        if (numberOfAccountsLeft <= 4) {
                            return investmentView.affinityAccount(newPaginatedAccount, new int[]{count}, null);
                        }
                        return investmentView.affinityAccount(newPaginatedAccount, new int[]{count}, "#. Next");
                    }
                    // first Option
                    else if (Integer.parseInt(baseRequest.userInput()) == 1) {
                        currentMenuSession.nextMenu = Menu.FUTURE_INFO;
                        currentMenuSession.persistOrUpdate();
                        return investmentView.createFuturePage1();
                    }

                    int index = Integer.parseInt(baseRequest.userInput()) - 2;
                    ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                    ClientAccountData customerDetails = middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
                    Map<String, String> accounts = helper.futureAccountMapper(customerDetails);
                    List<String> accountNumbers = new ArrayList<>(accounts.keySet());
                    List<String> accountTypes = new ArrayList<>(accounts.values());
                    String accountType = accountTypes.get(index);
                    String accountNumber = accountNumbers.get(index);
                    currentMenuSession.accNumber = accountNumber;
                    currentMenuSession.accType = accountType;
                    currentMenuSession.nextMenu = Menu.REQUEST_CERTIFICATE;
                    currentMenuSession.persistOrUpdate();
                    return investmentView.requestCertificate(accountType, accountNumber);
                }
                catch (InterruptedException | ExecutionException e) {
                    Log.error(e.getLocalizedMessage());
                    return genericViews.serverError();
                }
                catch (NumberFormatException nfe){
                    Log.error(nfe.getLocalizedMessage());
                    return genericViews.invalidInput();
                }
            }
            case REQUEST_CERTIFICATE -> {
                try {
                    switch (Integer.parseInt(baseRequest.userInput())) {
                        case 1 -> {
                            ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                            String accNumber = currentMenuSession.accNumber;
                            middleware.requestCertificate(accNumber, clientLoginResponse.token());
                            Log.debug("Confirm certificate request");
                            return genericViews.requestProcessing();
                        }
                        case 0 -> {
                            return genericViews.cancelRequest();
                        }
                        default -> {
                            return genericViews.invalidInput();
                        }
                    }
                }
                catch (NumberFormatException | BadRequestException e){
                    Log.error(e.getLocalizedMessage(), e);
                    return investmentView.certificateRequestFailed();
                }
                catch (InterruptedException | ExecutionException e){
                    Log.error(e.getLocalizedMessage(), e);
                    return genericViews.serverError();
                }
            }

            }
        return null;
    }

}
