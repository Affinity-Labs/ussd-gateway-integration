package com.affinitylabs.handler.impl;

import com.affinitylabs.exceptions.ServerErrorException;
import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.middleware.auth.CheckPhoneNumberRequest;
import com.affinitylabs.models.middleware.auth.CheckPhoneNumberResponse;
import com.affinitylabs.models.session.BlockedUsers;
import com.affinitylabs.models.session.Menu;
import com.affinitylabs.models.session.MenuSession;
import com.affinitylabs.services.Middleware;
import com.affinitylabs.views.LoginView;
import com.affinitylabs.views.MenuView;
import com.affinitylabs.views.OnboardingView;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@ApplicationScoped
public class InitHandler {

    @Inject
    Middleware middleware;

    @Inject
    MenuView menuView;

    @Inject
    OnboardingView onboardingView;

    public BaseResponse startUssd(BaseRequest baseRequest) throws ServerErrorException {
        Log.info("[" + baseRequest.msisdn() + "] Starting USSD on " + baseRequest.network());
        try {
            // Check if customer is blocked on the USSD
            BlockedUsers blockedUsers =  BlockedUsers.findbyPhoneNumber(baseRequest.msisdn());
            if (blockedUsers != null) {
                return new BaseResponse("Your account has been disabled. Contact our helpline on 0302 344 567 for assistance.", Actions.PROMPT);
            }
            CheckPhoneNumberRequest checkPhoneNumberRequest = new CheckPhoneNumberRequest(baseRequest.msisdn());
            CheckPhoneNumberResponse phoneNumberStatus = middleware.userStatus(checkPhoneNumberRequest);
            Log.info("[" + baseRequest.msisdn() + "] User status: " + phoneNumberStatus);
            String userStatus = phoneNumberStatus.status();
            MenuSession menuSession = new MenuSession();
            menuSession.sessionId = baseRequest.sessionId();
            menuSession.phoneNumber = baseRequest.msisdn();
            menuSession.mobileNetwork = baseRequest.network();
            menuSession.createdDate = LocalDateTime.now();
            switch (userStatus) {
                case "ACTIVE" -> {
                    menuSession.userIdNumber = phoneNumberStatus.userId();
                    menuSession.currentMenu = Menu.LOGIN;
                    menuSession.nextMenu = Menu.ENTER_PIN;
                    menuSession.persist();
                    Log.info("[" + baseRequest.msisdn() + "] User Started a new Session");
                    return new LoginView().login();
                }
                case "NEW_USER", "PHONENUMBER_NOT_REGISTERED" -> {
                    menuSession.userIdNumber = phoneNumberStatus.userId();
                    menuSession.currentMenu = Menu.CUSTOMER_INTEREST;
                    menuSession.nextMenu = Menu.CUSTOMER_INTEREST;
                    menuSession.persist();
                    Log.info("[" + baseRequest.msisdn() + "] User Started a new Session");
                    return menuView.customerInterest();
                }
                case "USER_BLOCKED" -> {
                    menuSession.userIdNumber = phoneNumberStatus.userId();
                    menuSession.currentMenu = Menu.BLOCKED_ACCOUNT;
                    menuSession.nextMenu = Menu.BLOCKED_ACCOUNT;
                    menuSession.persist();
                    Log.info("[" + baseRequest.msisdn() + "] User Started a new Session");
                    return menuView.blockedAccount();
                }
                case "PHONE_NUMBER_NOT_VERIFIED" -> {
                    menuSession.userIdNumber = phoneNumberStatus.userId();
                    menuSession.currentMenu = Menu.ONBOARD_GH_CARD;
                    menuSession.nextMenu = Menu.ONBOARD_GH_CARD;
                    menuSession.persist();
                    Log.info("[" + baseRequest.msisdn() + "] User Started a new Session");
                    return onboardingView.onboardGhCard();
                }
            }
        } catch (ServerErrorException e) {
            Log.error(e.getStackTrace());
            e.getBaseResponse();
        }

        return null;
    }
}
