package com.affinitylabs.views;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseResponse;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenericViews {

    public BaseResponse invalidInput() {
        return new BaseResponse("Invalid Input. Please try again later", Actions.PROMPT);
    }

    public BaseResponse requestProcessing () {
        return new BaseResponse("Your request is being processed. You will receive an SMS of your request shortly.\nThank you for using Affinity.", Actions.PROMPT);
    }

    public BaseResponse requestProcessingEmail () {
        return new BaseResponse("Your request is being processed. Your statement will be sent via email \nThank you for using Affinity.", Actions.PROMPT);
    }

    public BaseResponse invalidAmount(){
        return new BaseResponse("Invalid Amount. Please try again later", Actions.PROMPT);
    }

    public BaseResponse serverError(){
        return new BaseResponse("Something went wrong on our side. We’re working to fix it.", Actions.PROMPT);
    }

    public BaseResponse cancelRequest(){
        return new BaseResponse("Your request has been cancelled. Thank you for using Affinity.", Actions.PROMPT);
    }

    public BaseResponse invalidAccount(){
        return new BaseResponse("Invalid Account. Try again", Actions.PROMPT);
    }
}
