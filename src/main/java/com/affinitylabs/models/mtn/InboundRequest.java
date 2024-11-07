package com.affinitylabs.models.mtn;

public record InboundRequest(String sessionId, String messageType, String msisdn, String serviceCode, String ussdString,
                             String cellId, String language, String imsi) {
}
