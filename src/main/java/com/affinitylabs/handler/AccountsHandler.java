package com.affinitylabs.handler;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.middleware.accounts.*;
import com.affinitylabs.models.middleware.auth.ClientLoginResponse;
import com.affinitylabs.models.middleware.transactions.AccountStatementRequest;
import com.affinitylabs.models.session.Menu;
import com.affinitylabs.models.session.MenuSession;
import com.affinitylabs.services.AuthServer;
import com.affinitylabs.services.Middleware;
import com.affinitylabs.utilities.CacheConfigManager;
import com.affinitylabs.utilities.CacheKeysService;
import com.affinitylabs.utilities.Helper;
import com.affinitylabs.views.AccountView;
import com.affinitylabs.views.GenericViews;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;

@ApplicationScoped
public class AccountsHandler {

    @Inject
    AccountView accountView;


    @Inject
    Middleware middleware;

    @Inject
    Helper helper;

    @Inject
    GenericViews genericViews;

    @Inject
    CacheKeysService cacheKeysService;


    public BaseResponse handler(BaseRequest baseRequest)  {
        MenuSession currentMenuSession = MenuSession.findBySessionId(baseRequest.sessionId());
            switch (currentMenuSession.nextMenu){
                case AFFINITY_ACCOUNT -> {
                    try{
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
                            return accountView.affinityAccount(paginatedAccount, new int[]{1}, "#. Next");
                        }
                        return accountView.affinityAccount(helper.accountMapper(customerDetails), new int[]{1}, currentMenuSession.numberOfAccounts+1 + ". Add new growth Account" );
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
                                return accountView.affinityAccount(newPaginatedAccount, new int[]{count}, numberOfAccounts + 1 + ". Add new growth Account");
                            }
                            return accountView.affinityAccount(newPaginatedAccount, new int[]{count}, "#. Next");
                        }
                        // Last Option
                        else if (Integer.parseInt(baseRequest.userInput()) == currentMenuSession.numberOfAccounts + 1) {
                            currentMenuSession.nextMenu = Menu.ADD_NEW_GROWTH_ACCOUNT;
                            ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                            ClientAccountData customerDetails = middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
                            Map<String, String> customerAccounts = helper.dailyAccountMapper(customerDetails);
                            currentMenuSession.numberOfAccounts = customerAccounts.size();
                            currentMenuSession.persistOrUpdate();
                            if (customerAccounts.size() > 4) {
                                Map<String, String> paginatedAccount = helper.paginatedAccount(customerAccounts);
                                Map<String, String> accountLeft = helper.accountLeftMapper(customerAccounts, paginatedAccount);
                                cacheKeysService.initializeCacheLeft(baseRequest.sessionId(), accountLeft);
                                cacheKeysService.initializeCacheAccount(baseRequest.sessionId(), paginatedAccount);
                                return accountView.addNewGrowth(paginatedAccount, new int[]{1}, "#. Next");
                            }
                            return accountView.affinityAccount(helper.dailyAccountMapper(customerDetails), new int[]{1}, currentMenuSession.numberOfAccounts + 1 + ". Mobile wallet (" + baseRequest.msisdn() + ")");


                        }
                        int index = Integer.parseInt(baseRequest.userInput()) - 1;
                        ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                        ClientAccountData customerDetails = middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
                        Map<String, String> accounts = helper.accountMapper(customerDetails);
                        List<String> accountNumbers = new ArrayList<>(accounts.keySet());
                        List<String> accountTypes = new ArrayList<>(accounts.values());
                        currentMenuSession.accNumber = accountNumbers.get(index);
                        currentMenuSession.accType = accountTypes.get(index);
                        currentMenuSession.nextMenu = Menu.ACCOUNT_REQUEST;
                        currentMenuSession.persistOrUpdate();
                        return accountView.accountRequest();
                    }
                    catch (InterruptedException | ExecutionException e) {
                        Log.error(e.getLocalizedMessage(), e);
                        return genericViews.serverError();
                    }
                    catch (NumberFormatException nfe){
                        Log.error(nfe.getLocalizedMessage(), nfe);
                      return genericViews.invalidInput();
                    }
                }
                case ACCOUNT_REQUEST -> {
                    switch (baseRequest.userInput()) {
                        case "1" -> {
                            AccountBalance accountBalance = middleware.getAccountBalance(currentMenuSession.accNumber);
                            return accountView.accountBalance(currentMenuSession.accType, currentMenuSession.accNumber, accountBalance.availableBalance());
                        }
                        case "2" -> {
                            boolean emailVerified = middleware.getCustomer(baseRequest.msisdn()).emailVerified();
                            currentMenuSession.nextMenu = Menu.ACCOUNT_STATEMENT;
                            currentMenuSession.persistOrUpdate();
                            return accountView.accountStatement(emailVerified, currentMenuSession.accType, currentMenuSession.accNumber);
                        }
                        default -> {
                            return genericViews.invalidInput();
                        }
                    }
                }
                case ACCOUNT_STATEMENT -> {
                    boolean emailVerified = middleware.getCustomer(baseRequest.msisdn()).emailVerified();
                    if(emailVerified){
                        if (baseRequest.userInput().equals("1")) {
                            LocalDate startDate = LocalDate.now().minusDays(60);
                            LocalDate endDate = LocalDate.now();
                            AccountStatementRequest accountStatementRequest = new AccountStatementRequest(startDate, endDate, "SMS", currentMenuSession.accNumber);
                            middleware.accountStatement(accountStatementRequest);
                            return genericViews.requestProcessing();
                        }
                        return genericViews.invalidInput();
                    }
                    if (baseRequest.userInput().equals("1")) {
                        LocalDate startDate = LocalDate.now().minusDays(60);
                        LocalDate endDate = LocalDate.now();
                        AccountStatementRequest accountStatementRequest = new AccountStatementRequest(startDate, endDate, "SMS", currentMenuSession.accNumber);
                        middleware.accountStatement(accountStatementRequest);
                        return genericViews.requestProcessing();
                    } else if (baseRequest.userInput().equals("2")) {
                        LocalDate startDate = LocalDate.now().minusDays(60);
                        LocalDate endDate = LocalDate.now();
                        AccountStatementRequest accountStatementRequest = new AccountStatementRequest(startDate, endDate, "EMAIL", currentMenuSession.accNumber);
                        middleware.accountStatement(accountStatementRequest);
                        return genericViews.requestProcessingEmail();
                    }
                    else{
                        return genericViews.invalidInput();
                    }
                }

                case ADD_NEW_GROWTH_ACCOUNT -> {
                    try {
                    if (baseRequest.userInput().equals("#")){
                            Map<String, String> customerAccounts = cacheKeysService.getAccountLeft(baseRequest.sessionId()).get();
                            int numberOfAccounts = currentMenuSession.numberOfAccounts;
                            int numberOfAccountsLeft = customerAccounts.size();
                            int count = numberOfAccounts - numberOfAccountsLeft + 1;
                            Map<String, String> newPaginatedAccount = helper.paginatedAccount(customerAccounts);
                            Map<String, String> newCustomerAccount = helper.accountLeftMapper(customerAccounts, newPaginatedAccount);
                            cacheKeysService.initializeCacheLeft(baseRequest.sessionId(), newCustomerAccount);
                            cacheKeysService.initializeCacheAccount(baseRequest.sessionId(), newPaginatedAccount);
                            if (numberOfAccountsLeft <= 4){
                                return accountView.addNewGrowth(newPaginatedAccount, new int[]{count}, currentMenuSession.numberOfAccounts+1 + ". Mobile wallet ("+baseRequest.msisdn()+ ")");
                            }
                            return accountView.addNewGrowth(newPaginatedAccount, new int[]{count}, "#. Next");
                        // Last Option
                    } else if (Integer.parseInt(baseRequest.userInput()) == currentMenuSession.numberOfAccounts + 1) {
                        currentMenuSession.nextMenu = Menu.FUND_GROWTH_WITH_MOMO;
                        ClientLoginResponse clientLoginResponse = cacheKeysService.getCustomerToken(baseRequest.sessionId()).get();
                        ClientAccountData customerDetails = middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
                        Map<String, String> customerAccounts = helper.accountMapper(customerDetails);
                        currentMenuSession.numberOfAccounts = customerAccounts.size();
                        currentMenuSession.persistOrUpdate();
                        return accountView.selectMobileNetwork(baseRequest.msisdn());
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
                    return accountView.amtToTransferNewAccount(accountTypes.get(index), accountNumbers.get(index));
                    } catch (InterruptedException | ExecutionException e) {
                        Log.error(e.getLocalizedMessage(), e);
                        return genericViews.serverError();
                    }
                    catch (NumberFormatException nfe){
                        Log.error(nfe.getLocalizedMessage(), nfe);
                        return genericViews.invalidInput();
                    }
                }
                case FUND_NEW_GROWTH, AMOUNT_TO_PAY_MOMO -> {
                    try {
                        float amount = Float.parseFloat(baseRequest.userInput());
                        if(amount < 20){
                            return new BaseResponse("Amount must be greater than GHS 20", Actions.PROMPT);
                        }
                        if (currentMenuSession.nextMenu == Menu.AMOUNT_TO_PAY_MOMO){
                            currentMenuSession.amountToTransfer = amount;
                            currentMenuSession.nextMenu = Menu.NAME_GROWTH_ACCOUNT_MOMO;
                            currentMenuSession.persistOrUpdate();
                            return accountView.nameNewAccount();
                        }
                        currentMenuSession.nextMenu = Menu.NAME_GROWTH_ACCOUNT;
                        currentMenuSession.amountToTransfer = amount;
                        currentMenuSession.persistOrUpdate();
                        return accountView.nameNewAccount();
                    }
                    catch (NumberFormatException e)
                    {
                        Log.error(e.getLocalizedMessage(), e);
                        return genericViews.invalidAmount();
                    }
                    }
                case FUND_GROWTH_WITH_MOMO -> {
                    try {
                        switch (baseRequest.userInput()) {
                            case "1" -> {
                                currentMenuSession.serviceCode = "MTN_MM";
                                return accountView.amtToTransferMomo("MTN");
                            }
                            case "2" -> {
                                currentMenuSession.serviceCode = "VODA_MM";
                                return accountView.amtToTransferMomo("Telecel");
                            }
                            case "3" -> {
                                currentMenuSession.serviceCode = "AIRTELTIGO_MM";
                                return accountView.amtToTransferMomo("AirtelTigo");
                            }
                            case "0" -> {
                                // Go back
                            }
                            default -> {
                                return genericViews.invalidInput();
                            }
                        }
                    }
                    catch (Exception e){
                        Log.error(e.getLocalizedMessage(), e);
                        return genericViews.serverError();
                    }
                    finally {
                        currentMenuSession.nextMenu = Menu.AMOUNT_TO_PAY_MOMO;
                        currentMenuSession.persistOrUpdate();
                    }
                }

                case NAME_GROWTH_ACCOUNT_MOMO -> {
                    currentMenuSession.receiptName = baseRequest.userInput();
                    currentMenuSession.nextMenu = Menu.FUND_AFFINITY_GROWTH;
                    currentMenuSession.persistOrUpdate();
                    return accountView.confirmNewAccountMomo(currentMenuSession.amountToTransfer, currentMenuSession.phoneNumber, currentMenuSession.mobileNetwork);
                }
                case NAME_GROWTH_ACCOUNT -> {
                    currentMenuSession.receiptName = baseRequest.userInput();
                    currentMenuSession.nextMenu = Menu.FUND_AFFINITY_GROWTH;
                    currentMenuSession.persistOrUpdate();
                    return accountView.confirmNewAccount(currentMenuSession.amountToTransfer, currentMenuSession.accNumber);
                }
                case FUND_AFFINITY_GROWTH -> {
                    try {
                        String token = cacheKeysService.getCustomerToken(currentMenuSession.sessionId).get().token();
                        if(Objects.equals(baseRequest.userInput(), "1")){
                            if (currentMenuSession.serviceCode != null) {
                                // confirm momo
                                UUID uuid = UUID.randomUUID();
                                Funding funding = new Funding("MOBILE_MONEY", currentMenuSession.amountToTransfer, currentMenuSession.phoneNumber, currentMenuSession.serviceCode, currentMenuSession.phoneNumber, uuid.toString());
                                NewAccount newAccount = new NewAccount("PERSONAL", "GROWTH", funding, currentMenuSession.customerId, currentMenuSession.receiptName);
                                middleware.createNewAccount(token, newAccount);
                                return genericViews.requestProcessing();
                            }
                            // confirm daily
                            UUID uuid = UUID.randomUUID();
                            Funding funding = new Funding("OWN_ACCOUNT", currentMenuSession.amountToTransfer, currentMenuSession.phoneNumber, "", currentMenuSession.accNumber, uuid.toString());
                            NewAccount newAccount = new NewAccount("PERSONAL", "GROWTH", funding, currentMenuSession.customerId, currentMenuSession.receiptName);
                            middleware.createNewAccount(token, newAccount);
                            return genericViews.requestProcessing();
                        }

                    }
                    catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }

                }

            }
        return null;
    }
}
