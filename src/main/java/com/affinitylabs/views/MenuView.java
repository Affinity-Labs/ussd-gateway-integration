package com.affinitylabs.views;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;

@ApplicationScoped
public class MenuView {

    public BaseResponse customerInterest() {
        ArrayList<String> options = new ArrayList<>();
        options.add("1. Open an Account");
        BaseResponse baseResponse = new BaseResponse("Welcome to Affinity. What are you interested in?", Actions.SHOWMENU);
        baseResponse.setOptions(options);
        return baseResponse;
    }

    public BaseResponse mainMenu(String firstName) {
        ArrayList<String> options = new ArrayList<>();
        options.add("1. Affinity accounts");
        options.add("2. Investments");
        options.add("3. Send money");
        options.add("4. Make payments");
        options.add("5. Loans");
        options.add("6. Approvals");
        options.add("7. Change PIN");
        options.add("8. Help");
        BaseResponse baseResponse = new BaseResponse("Hello " + firstName + ", please select your request:", Actions.SHOWMENU);
        baseResponse.setOptions(options);
        return baseResponse;
    }

    public BaseResponse blockedAccount() {
        return new BaseResponse("Your account has been blocked. Contact our helpline on 0302 344 567 for assistance.", Actions.PROMPT);
    }
}
