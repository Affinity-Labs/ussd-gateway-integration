package com.affinitylabs.models.mtn;

public record InboundResponse(String statusCode, String statusMessage, String transactionId, InboundResponseData data,
                              InboundResponseLink _link) {
}
