package com.affinitylabs.handler;

import com.affinitylabs.exceptions.ServerErrorException;
import com.affinitylabs.handler.impl.ResponseHandler;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.middleware.accounts.Customer;
import com.affinitylabs.models.session.Menu;
import com.affinitylabs.models.session.MenuSession;
import com.affinitylabs.services.Middleware;
import com.affinitylabs.utilities.Helper;
import com.affinitylabs.views.AccountView;
import com.affinitylabs.views.MenuView;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MainMenuHandler {

    @Inject
    ResponseHandler responseHandler;


    public BaseResponse handler(BaseRequest baseRequest) {
        try{
            MenuSession currentUserSession = MenuSession.findBySessionId(baseRequest.sessionId());
            switch (Integer.parseInt(baseRequest.userInput())){
                case 1 -> {
                    currentUserSession.currentMenu = Menu.AFFINITY_ACCOUNT;
                    currentUserSession.nextMenu = Menu.AFFINITY_ACCOUNT;
                    currentUserSession.persistOrUpdate();
                    return responseHandler.handle(baseRequest);
                }
                case 2 -> {
                    currentUserSession.currentMenu = Menu.INVESTMENTS;
                    currentUserSession.nextMenu = Menu.INVESTMENTS;
                    currentUserSession.persistOrUpdate();
                    return responseHandler.handle(baseRequest);
                }
                case 3 -> {
                    currentUserSession.currentMenu = Menu.SEND_MONEY;
                    currentUserSession.nextMenu = Menu.SEND_MONEY;
                    currentUserSession.persistOrUpdate();
                    return responseHandler.handle(baseRequest);
                }
            }
        }
        catch (ServerErrorException e){
            Log.error(e.getLocalizedMessage(), e);
            return e.getBaseResponse();
        }
        return null;
    }
}
