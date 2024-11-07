package com.affinitylabs.models.middleware.accounts;

import java.util.List;

public record Customer(String customerId, String firstName, String lastName, String otherNames, String email, String phoneNumber, String sex, String status, String dateOfBirth, CustomerEmployment employment, List<CustomerAccounts> accounts, List<CustomerDocuments> documents, String dateCreated, CustomerAddress address, Boolean emailVerified, String profilePictureUrl, String customerProfileCreationDate, String secondaryPhoneNumber, String countryOfBirth, String nationality, String maritalStatus, NextOfKin nextOfKin, String signatureUrl, String title, String dailyAccountApprovalDate, String kycLevel, CustomerAssignedBranch assignedBranch, String assignedRelationshipOfficer, String assignedStatus, String mambuClientId, String mambuClientKey, String selfieUrl, List<String> mobileMoneyNumbers) {
}
