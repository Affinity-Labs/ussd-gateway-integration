package com.affinitylabs.handler;

import com.affinitylabs.exceptions.BadRequestException;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.middleware.accounts.ClientAccountData;
import com.affinitylabs.models.middleware.auth.ClientLoginResponse;
import com.affinitylabs.models.middleware.transactions.QueryServiceRequest;
import com.affinitylabs.models.middleware.transactions.QueryServiceResponse;
import com.affinitylabs.models.session.Menu;
import com.affinitylabs.models.session.MenuSession;
import com.affinitylabs.services.Middleware;
import com.affinitylabs.services.Transactions;
import com.affinitylabs.utilities.CacheKeysService;
import com.affinitylabs.utilities.Helper;
import com.affinitylabs.views.GenericViews;
import com.affinitylabs.views.SendMoneyView;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;


@ApplicationScoped
public class SendMoneyHandler {

    @Inject
    SendMoneyView sendMoneyView;

    @Inject
    GenericViews genericViews;

    @Inject
    Transactions transactions;

    @Inject
    CacheKeysService cacheKeysService;

    @Inject
    Helper helper;

    @Inject
    Middleware middleware;

    @ConfigProperty(name = "affinity.gipcode")
    String affinityGipCode;


    public BaseResponse handler(BaseRequest baseRequest) {
        MenuSession currentMenuSession = MenuSession.findBySessionId(baseRequest.sessionId());
        switch (currentMenuSession.nextMenu){
            case SEND_MONEY -> {
                currentMenuSession.nextMenu = Menu.SELECT_SEND_MONEY_CHANNEL;
                currentMenuSession.persistOrUpdate();
                return sendMoneyView.sendMoneyChannel();
            }

            case SELECT_SEND_MONEY_CHANNEL -> {
                try{
                    switch (Integer.parseInt(baseRequest.userInput())){
                        case 1 -> {
                            currentMenuSession.nextMenu = Menu.SEND_MONEY_ANY_AFFINITY_ACCOUNT;
                            currentMenuSession.serviceCode = "INTRABANK_TRANSFER_OUTWARD";
                            currentMenuSession.persistOrUpdate();
                            return sendMoneyView.sendMoneyAnyAccount();
                        }
                        case 2 -> {
                            currentMenuSession.nextMenu = Menu.SEND_TO_MOMO;
                            currentMenuSession.persistOrUpdate();
                            return sendMoneyView.sendMoneyToMomo();
                        }
                        case 3 -> {
                           currentMenuSession.nextMenu = Menu.SEND_TO_BANK;
                           currentMenuSession.serviceCode = "BANK_TRANSFER";
                        }
                    }
                }
                catch (NumberFormatException e)
                {
                    Log.error(e.getLocalizedMessage(), e);
                    return genericViews.invalidInput();
                }
            }
            case SEND_MONEY_ANY_AFFINITY_ACCOUNT -> {
                try {
                    ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                    QueryServiceRequest queryServiceRequest = new QueryServiceRequest(currentMenuSession.serviceCode, baseRequest.userInput(), affinityGipCode);
                    QueryServiceResponse queryServiceResponse = transactions.queryService(clientLoginResponse.token(), queryServiceRequest);
                    if(Objects.equals(queryServiceResponse.accountName(), "")){
                        return genericViews.invalidAccount();
                    }
                    currentMenuSession.receiptName = queryServiceResponse.accountName();
                    currentMenuSession.nextMenu = Menu.AMT_TO_SEND_TO_ANY_AFFINITY_ACCOUNT;
                    currentMenuSession.receiptAccNumber = baseRequest.userInput();
                    currentMenuSession.persistOrUpdate();
                    return sendMoneyView.amtToSend(queryServiceResponse.accountName(), baseRequest.userInput());
                }
                catch (InterruptedException | ExecutionException e){
                    Log.error(e.getLocalizedMessage(), e);
                    return genericViews.serverError();
                }
                catch (BadRequestException e){
                    Log.error(e.getLocalizedMessage(), e);
                    return genericViews.invalidAccount();
                }
            }
            case AMT_TO_SEND_TO_ANY_AFFINITY_ACCOUNT -> {
                try{
                    if(helper.isValidAmount(baseRequest.userInput())){
                        currentMenuSession.amountToTransfer = Float.parseFloat(baseRequest.userInput());
                        currentMenuSession.nextMenu = Menu.ACC_TO_SEND_MONEY_FROM;
                        ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                        ClientAccountData customerDetails = middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
                        currentMenuSession.nextMenu = Menu.SELECT_ACCOUNT;
                        Map<String, String> customerAccounts = helper.accountMapper(customerDetails);
                        currentMenuSession.numberOfAccounts = customerAccounts.size();
                        currentMenuSession.persistOrUpdate();
                        if (customerAccounts.size() > 4){
                            Map<String, String> paginatedAccount = helper.paginatedAccount(customerAccounts);
                            Map<String, String> accountLeft =  helper.accountLeftMapper(customerAccounts, paginatedAccount);
                            cacheKeysService.initializeCacheLeft(baseRequest.sessionId(), accountLeft);
                            cacheKeysService.initializeCacheAccount(baseRequest.sessionId(), paginatedAccount);
                            return sendMoneyView.accSendMoney(paginatedAccount, new int[]{1}, "#. Next");
                        }
                        return sendMoneyView.accSendMoney(helper.accountMapper(customerDetails), new int[]{1}, currentMenuSession.numberOfAccounts+1 + ". Add new growth Account" );
                    }
                    else {
                        return genericViews.invalidAmount();
                    }
                } catch (InterruptedException | ExecutionException e){
                    Log.error(e.getLocalizedMessage(), e);
                    return genericViews.serverError();
                }
            }
            case ACC_TO_SEND_MONEY_FROM -> {
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
                            return sendMoneyView.accSendMoney(newPaginatedAccount, new int[]{count}, null);
                        }
                        return sendMoneyView.accSendMoney(newPaginatedAccount, new int[]{count}, "#. Next");
                    }
                    int index = Integer.parseInt(baseRequest.userInput()) - 1;
                    ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                    ClientAccountData customerDetails = middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
                    Map<String, String> accounts = helper.dailyAccountMapper(customerDetails);
                    List<String> accountNumbers = new ArrayList<>(accounts.keySet());
                    List<String> accountTypes = new ArrayList<>(accounts.values());
                    currentMenuSession.accNumber = accountNumbers.get(index);
                    currentMenuSession.accType = accountTypes.get(index);
                    currentMenuSession.nextMenu = Menu.FUND_NEW_GROWTH;
                    currentMenuSession.persistOrUpdate();
                }catch (InterruptedException | ExecutionException e) {
                    Log.error(e.getLocalizedMessage(), e);
                    return genericViews.serverError();
                }
                catch (NumberFormatException nfe){
                    Log.error(nfe.getLocalizedMessage(), nfe);
                    return genericViews.invalidInput();
                }
            }
        }
        return null;
    }
}
