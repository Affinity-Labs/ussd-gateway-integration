package com.affinitylabs;

import com.affinitylabs.handler.gateway.MTNHandler;
import com.affinitylabs.handler.impl.InitHandler;
import com.affinitylabs.handler.impl.ResponseHandler;
import com.affinitylabs.models.BaseResponse;
import com.affinitylabs.models.mtn.InboundRequest;
import com.affinitylabs.models.mtn.InboundResponse;
import com.affinitylabs.models.session.MenuSession;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Objects;

@Path("/ussd")
public class USSDEntry {

    @Inject
    InitHandler initHandler;

    @Inject
    ResponseHandler responseHandler;

    @ConfigProperty(name = "APP_AUTH_KEY")
    String appAuthKey;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/mtn/callback/")
    public InboundResponse mtncallback(@QueryParam("x_token") String x_token, InboundRequest inboundRequest) {
        if (Objects.equals(appAuthKey, x_token)) {
            MTNHandler mtnHandler = new MTNHandler(inboundRequest);
            MenuSession currentMenuSession = MenuSession.findBySessionId(inboundRequest.sessionId());
            if (currentMenuSession == null) {
                Log.info("Starting a new session");
                BaseResponse baseResponse = initHandler.startUssd(mtnHandler.toBaseRequest(inboundRequest));
                return mtnHandler.toGatewayResponse(baseResponse);
            }
            Log.info("Found the session Id");
            BaseResponse baseResponse = responseHandler.handle(mtnHandler.toBaseRequest(inboundRequest));
            return mtnHandler.toGatewayResponse(baseResponse);
        } else {
            return null;
        }
    }

}
