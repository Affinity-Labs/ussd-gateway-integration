package com.affinitylabs.models.session;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class MenuSession extends PanacheMongoEntity {
    public String sessionId;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String customerId;
    public Menu currentMenu;
    public Menu nextMenu;
    public String accNumber;
    public String accType;
    public String accountId;
    public float totalBalance;
    public String mobileNetwork;
    public float amountToTransfer;
    public String receiptName;
    public String receiptAccNumber;
    public String referenceNumber;
    public String serviceCode;
    public String uuid;
    public int futurePeriod;
    public String futureInterest;
    public String futureMaturityDate;
    public int loginAttempt;
    public String userOtp;
    public String userPin;
    public String userIdNumber;
    public String currentPin;
    public String newPin;
    public boolean deleted;
    public LocalDateTime createdDate;
    public int numberOfAccounts;

    public static MenuSession findBySessionId(String sessionId) {
        return MenuSession.find("sessionId", sessionId).firstResult();
    }

    public void findUpdateBySessionId(String sessionId, MenuSession menuSession) {
        MenuSession.update("sessionId", sessionId, menuSession);
    }

}
