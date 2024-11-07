package com.affinitylabs.handler.impl;

import com.affinitylabs.exceptions.ServerErrorException;
import com.affinitylabs.handler.*;
import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.session.MenuSession;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class ResponseHandler {
    @Inject
    LoginHandler loginHandler;

    @Inject
    MainMenuHandler mainMenuHandler;

    @Inject
    AccountsHandler accountsHandler;

    @Inject
    InvestmentHandler investmentHandler;

    @Inject
    SendMoneyHandler sendMoneyHandler;

    public BaseResponse handle(BaseRequest baseRequest) {
        MenuSession currentMenuSession = MenuSession.findBySessionId(baseRequest.sessionId());
        switch (currentMenuSession.currentMenu) {
            case LOGIN -> {
                if (currentMenuSession.loginAttempt >= 2) {
                    return loginHandler.disableAccount(baseRequest);
                }
                return loginHandler.login(baseRequest);
            }
            case MAIN_MENU -> {
                return mainMenuHandler.handler(baseRequest);
            }
            case AFFINITY_ACCOUNT -> {
                return accountsHandler.handler(baseRequest);
            }
            case INVESTMENTS -> {
                return investmentHandler.handler(baseRequest);
            }
            case SEND_MONEY -> {
                return sendMoneyHandler.handler(baseRequest);
            }

            default -> {
                return new BaseResponse("No switch statement", Actions.PROMPT);
            }
        }
    }

}
