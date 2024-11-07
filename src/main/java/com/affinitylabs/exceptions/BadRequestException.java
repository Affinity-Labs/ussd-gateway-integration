package com.affinitylabs.exceptions;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BaseResponse getBaseResponse() {
        return new BaseResponse("Something went wrong on our side. We’re working to fix it.", Actions.PROMPT);
    }
}
