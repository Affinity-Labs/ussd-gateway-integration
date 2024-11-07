package com.affinitylabs.views;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;
import jakarta.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class OnboardingView {

    public BaseResponse onboardGhCard() {
        return new BaseResponse("Welcome to Affinity\n\nPlease enter your National ID eg. GHA-XXXXXXXX-X", Actions.INPUT);
    }
}
