package com.affinitylabs.models.middleware.accounts;

public record CustomerDocuments(String documentUrl, String documentType, String documentSubType, String documentNumber, String dateCreated, String secondaryDocumentUrl) {
}
