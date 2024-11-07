package com.affinitylabs.models.mtn;

public record InboundResponseData(String inboundResponse, Boolean userInputRequired, Integer messageType,
                                  String serviceCode, String msisdn) {

}
