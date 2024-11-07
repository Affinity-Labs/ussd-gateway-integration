package com.affinitylabs.models.middleware.auth;

public record ValidatePasswordResponse(String firstName, String lastName, String otherNames, String phoneNumber,
                                       String customerId) {
//    public  ValidatePasswordResponse(JSONObject jsonObject){
//        this(jsonObject.getString("firstName"),)
//    }
}
