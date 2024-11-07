package com.affinitylabs.handler.gateway;

import com.affinitylabs.models.Actions;
import com.affinitylabs.models.BaseRequest;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.mtn.*;
import io.quarkus.logging.Log;

import java.util.ArrayList;

public class MTNHandler {
    InboundRequest inboundRequest;

    public MTNHandler(InboundRequest inboundRequest) {
        this.inboundRequest = inboundRequest;
    }

    public BaseRequest toBaseRequest(InboundRequest inboundRequest) {
        return new BaseRequest(inboundRequest.sessionId(), inboundRequest.msisdn(), inboundRequest.ussdString(), "MTN_GH");
    }

    public InboundResponse toGatewayResponse(BaseResponse baseResponse) {
        Actions actions = baseResponse.getActions();
        ArrayList<String> optionList;
        int messageType = 0;
        boolean inputRequired = false;
        switch (actions.toString()) {
            case "INPUT", "SHOWMENU" -> {
                messageType = 1;
                inputRequired = true;
            }
            case "PROMPT" -> messageType = 2;
        }
        if (baseResponse.getOptions() == null) {
            optionList = new ArrayList<>();
        } else {
            optionList = baseResponse.getOptions();
        }
        String responseMessage = baseResponse.getTitle() + "\n" + String.join("\n", optionList);
        InboundResponseData inboundResponseData = new InboundResponseData(responseMessage, inputRequired, messageType, this.inboundRequest.serviceCode(), this.inboundRequest.msisdn());
        InboundResponseSelf inboundResponseSelf = new InboundResponseSelf("localhost:8080");
        Log.info("Hitting the handler");
        InboundResponseLink inboundResponseLink = new InboundResponseLink(inboundResponseSelf);
        return new InboundResponse("200", "Hello Testing", "transId", inboundResponseData, inboundResponseLink);

    }
}
