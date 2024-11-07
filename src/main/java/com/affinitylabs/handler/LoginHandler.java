package com.affinitylabs.handler;

import com.affinitylabs.exceptions.AuthException;
import com.affinitylabs.exceptions.ServerErrorException;
import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.middleware.auth.ClientLoginRequest;
import com.affinitylabs.models.middleware.auth.ClientLoginResponse;
import com.affinitylabs.models.middleware.auth.ValidatePasswordResponse;
import com.affinitylabs.models.session.BlockedUsers;
import com.affinitylabs.models.session.Menu;
import com.affinitylabs.models.session.MenuSession;
import com.affinitylabs.models.session.Status;
import com.affinitylabs.services.AuthServer;
import com.affinitylabs.services.Middleware;
import com.affinitylabs.views.MenuView;
import io.quarkus.logging.Log;
import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;



@ApplicationScoped
public class LoginHandler {

    @Inject
    Middleware middleware;

    @Inject
    AuthServer authServer;

    @Inject
    MenuView menuView;

    public BaseResponse login(BaseRequest baseRequest) throws AuthException, ServerErrorException {
        try {
            MenuSession currentMenuSession = MenuSession.findBySessionId(baseRequest.sessionId());
            ClientLoginRequest clientLoginRequest = new ClientLoginRequest("+" + baseRequest.msisdn(), baseRequest.userInput());
            ValidatePasswordResponse validatedCustomer = middleware.validateCustomer(clientLoginRequest, baseRequest.sessionId());
            ClientLoginResponse clientLoginResponse = authServer.customerLogin(clientLoginRequest, baseRequest.sessionId());
            middleware.getCustomerAccounts(clientLoginResponse.token(), baseRequest.msisdn());
            //todo: Check first time login
            currentMenuSession.currentMenu = Menu.MAIN_MENU;
            currentMenuSession.nextMenu = Menu.MAIN_MENU;
            currentMenuSession.firstName = validatedCustomer.firstName();
            currentMenuSession.lastName = validatedCustomer.lastName();
            currentMenuSession.persistOrUpdate();
            Log.info(baseRequest);
            return menuView.mainMenu(currentMenuSession.firstName);
        } catch (AuthException authException) {
            MenuSession currentMenuSession = MenuSession.findBySessionId(baseRequest.sessionId());
            currentMenuSession.loginAttempt += 1;
            currentMenuSession.persistOrUpdate();
            Log.debug("Failed to validate customer: " + baseRequest.msisdn() + "on attempt: " + currentMenuSession.loginAttempt);
            return authException.getBaseResponse(currentMenuSession.loginAttempt);
        } catch (ServerErrorException e) {
            Log.error(e.getLocalizedMessage(), e);
            return e.getBaseResponse();
        }
    }

    public BaseResponse disableAccount(BaseRequest baseRequest){
        MenuSession currentMenuSession = MenuSession.findBySessionId(baseRequest.sessionId());
        currentMenuSession.currentMenu = Menu.BLOCKED_ACCOUNT;
        currentMenuSession.loginAttempt += 1;
        currentMenuSession.persistOrUpdate();
        BlockedUsers blockedUsers = new BlockedUsers();
        blockedUsers.phoneNumber = baseRequest.msisdn();
        blockedUsers.status = Status.BLOCKED;
        blockedUsers.createdDate = LocalDateTime.now();
        blockedUsers.persistOrUpdate();
        return new BaseResponse("Your account has been disabled. Contact our helpline on 0302 344 567 for assistance.", Actions.PROMPT);
    }

}
