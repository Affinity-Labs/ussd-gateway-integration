package com.affinitylabs.models.middleware.accounts;


public class NewAccount {
    private String accountType;
    private String subType;
    Funding funding;
    private String customerId;
    private String alias;
    private Future future;


    public NewAccount(String accountType, String subType, Funding funding, String customerId, String alias) {
        this.accountType = accountType;
        this.subType = subType;
        this.customerId = customerId;
        this.alias = alias;
        this.funding = funding;
    }

    // Getter Methods
    public String getAccountType() {
        return accountType;
    }

    public String getSubType() {
        return subType;
    }

    public Funding getFunding() {
        return funding;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAlias() {
        return alias;
    }

    public Future getFuture() {
        return future;
    }
    // Setter Methods
    public void setAccountType( String accountType ) {
        this.accountType = accountType;
    }

    public void setSubType( String subType ) {
        this.subType = subType;
    }

    public void setFunding( Funding fundingObject ) {
        this.funding = fundingObject;
    }

    public void setCustomerId( String customerId ) {
        this.customerId = customerId;
    }

    public void setAlias( String alias ) {
        this.alias = alias;
    }

    public void setFuture(Future future) {
        this.future = future;
    }
}
