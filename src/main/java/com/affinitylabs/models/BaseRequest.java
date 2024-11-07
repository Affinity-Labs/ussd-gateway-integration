package com.affinitylabs.models;

import java.util.List;

public record BaseRequest(String sessionId, String msisdn, String userInput, String network) {
}
