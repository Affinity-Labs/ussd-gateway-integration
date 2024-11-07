package com.affinitylabs.views;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;


public class LoginView {
    public BaseResponse login() {
        return new BaseResponse("Welcome to Affinity.\n\n Please enter your 6-digit PIN to continue.", Actions.INPUT);
    }
}
