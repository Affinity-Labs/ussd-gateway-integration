package com.affinitylabs.exceptions;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }

    public BaseResponse getBaseResponse(int loginAttempts) {
        int loginAttemptsRemaining = 3 - loginAttempts;
        return new BaseResponse("Wrong Password. Try again.\n\nYou have " + loginAttemptsRemaining + " attempts left", Actions.INPUT);
    }
}
